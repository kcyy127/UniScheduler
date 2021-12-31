const functions = require("firebase-functions");
const admin = require("firebase-admin");
const FieldValue = require('firebase-admin').firestore.FieldValue;
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

admin.initializeApp();
const db = admin.firestore();

// exports.helloWorld = functions.region('us-west2').https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.updateUniversityList = functions.region('us-west2').firestore.document('/universities/{docId}').onCreate((snap, context) => {
    const uniName = snap.data().name;

    functions.logger.log('Updating Uni Name List', context.params.docId);
    return db.doc('general/university_list').update({
        universities: FieldValue.arrayUnion(uniName)
    });

})


// add schedule events to user when enrolled_sections updated
exports.updateUserSchedule = functions.region('us-west2').firestore.document('/users/{userId}/{uniId}/{sectionId}').onCreate((snap, context) => {
  const ourUserId = context.params.userId;
  const batch = db.batch();
  functions.logger.log("Creating schedule events from section addition", ourUserId);
  const arrOfSchedules = Array.from(snap.data().schedules);

  for (let i = 0; i < arrOfSchedules.length; i++) {
    arrOfSchedules[i].course_affil = context.params.sectionId;
    functions.logger.log(arrOfSchedules[i]);
    batch.set(db.collection("users").doc(ourUserId).collection("schedules").doc(), arrOfSchedules[i]);
  }
  return batch.commit();

})

// delete all course affiliated stuff
exports.deleteSectionAffiliated = functions.region('us-west2').firestore.document('/users/{userId}/{uniId}/{sectionId}').onDelete(async (snap, context) => {
  try {
    const batch = db.batch();
    // find schedules and add to delete batch
    const scheduleRef = db.collection("users").doc(context.params.userId).collection("schedules");
    const scheduleSnap = await scheduleRef.where('course_affil', '==', context.params.sectionId).get();
    scheduleSnap.forEach((doc) => {
      batch.delete(scheduleRef.doc(doc.id));
    })
    // find tasks and add to delete batch
    const taskRef = db.collection("users").doc(context.params.userId).collection("tasks");
    const taskSnap = await taskRef.where('course_affil', '==', context.params.sectionId).get();
    taskSnap.forEach((doc) => {
      batch.delete(taskRef.doc(doc.id));
    })

    batch.commit();
    } catch (error) {
      functions.logger.log(error);
    }
})
