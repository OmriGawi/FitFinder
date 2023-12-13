const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.checkForMatch = functions.firestore
    .document('users/{userId}')
    .onUpdate(async (change, context) => {
        const beforeData = change.before.data();
        const afterData = change.after.data();
        const userId = context.params.userId;

        // Check if the 'accepted' field has changed
        if (JSON.stringify(beforeData.accepted) !== JSON.stringify(afterData.accepted)) {
            // Get the newly accepted user
            const newAcceptedUsers = afterData.accepted.filter(user => !beforeData.accepted.includes(user));

            for (const acceptedUser of newAcceptedUsers) {
                // Check if the acceptedUser also accepted the current user
                const acceptedUserData = await admin.firestore().collection('users').doc(acceptedUser).get();
                const acceptedUserAcceptedList = acceptedUserData.data().accepted;

                if (acceptedUserAcceptedList && acceptedUserAcceptedList.includes(userId)) {
                    // Create a new match
                    const newMatch = await admin.firestore().collection('matches').add({
                        user1: userId,
                        user2: acceptedUser,
                        timestamp: admin.firestore.FieldValue.serverTimestamp()
                    });

                    // Add the match ID to both users' "matches" fields
                    const matchId = newMatch.id;
                    await admin.firestore().collection('users').doc(userId).update({
                        matches: admin.firestore.FieldValue.arrayUnion(matchId)
                    });
                    await admin.firestore().collection('users').doc(acceptedUser).update({
                        matches: admin.firestore.FieldValue.arrayUnion(matchId)
                    });
                }
            }
        }

        return null;
    });


    exports.sendMatchNotification = functions.firestore
    .document('matches/{matchId}')
    .onCreate(async (snapshot, context) => {
        console.log("Function triggered with match ID:", context.params.matchId);
        
        // Extract the user1 and user2 from the added match document
        const user1 = snapshot.data().user1;
        const user2 = snapshot.data().user2;
    
        console.log(`Extracted users: User1 = ${user1}, User2 = ${user2}`);
        
        try {
            // Fetch the FCM tokens for each user
            const user1Doc = await admin.firestore().collection('users').doc(user1).get();
            const user2Doc = await admin.firestore().collection('users').doc(user2).get();
      
            const user1FcmToken = user1Doc.data().fcmToken;
            const user2FcmToken = user2Doc.data().fcmToken;
    
            console.log(`Fetched FCM tokens: User1 FCM Token = ${user1FcmToken}, User2 FCM Token = ${user2FcmToken}`);
            
            // Create a notification payload
            const payload = {
                notification: {
                    title: 'New Match!',
                    body: 'You have a new match. Check it out!',
                    clickAction: 'FLUTTER_NOTIFICATION_CLICK',
                },
                data: {
                    type: 'match'
                }
            };
      
            // Send notifications to the FCM tokens
            const response = await admin.messaging().sendToDevice([user1FcmToken, user2FcmToken], payload);
            console.log("Notification response:", response);
        } catch(error) {
            console.error("Error in function:", error);
        }
        
        return null;
    });


    exports.onInviteCreated = functions.firestore
        .document('invites/{inviteId}')
        .onCreate(async (snapshot, context) => {
            const inviteData = snapshot.data();
            const inviteId = context.params.inviteId;

            // Add invite ID to the receiver's 'invites' field
            const receiverUpdate = admin.firestore().collection('users').doc(inviteData.receiverId).update({
                invites: admin.firestore.FieldValue.arrayUnion(inviteId)
            });

            return receiverUpdate;
        });

    
