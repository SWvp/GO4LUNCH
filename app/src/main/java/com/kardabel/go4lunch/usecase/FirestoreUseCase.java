package com.kardabel.go4lunch.usecase;


import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;

public class FirestoreUseCase {

//  private static volatile FirestoreUseCase instance;

//  public FirestoreUseCase() {
//  }

//  public static FirestoreUseCase getInstance() {
//      FirestoreUseCase result = instance;
//      if (result != null) {
//          return result;
//      }
//      synchronized (FirestoreUseCase.class) {
//          if (instance == null) {
//              instance = new FirestoreUseCase();
//          }
//          return instance;
//      }
//  }

    //public Task<Void> signOut(Context context) {
     //   return AuthUI.getInstance().signOut(context);
    //}



    //  @Nullable
    //  public String getCurrentUserUID() {
    //      FirebaseUser user = GetCurrentUserUseCase.getCurrentUser();
    //      return (user != null) ? user.getUid() : null;

 //  }

 // public Task<Void> deleteUser(Context context) {
 //     return AuthUI.getInstance().delete(context);
 // }

 // // Get the Collection Reference
 // private CollectionReference getUsersCollection() {
 //     return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
 // }

 // // UPDATE USERS NAME
 // public Task<Void> updateUsername(String username) {
 //     String uid = this.getCurrentUserUID();
 //     if (uid != null) {
 //         return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
 //     } else {
 //         return null;
 //     }
 // }

 // // DELETE USER FROM FIRESTORE
 // public void deleteUserFromFirestore() {
 //     String uid = this.getCurrentUserUID();
 //     if (uid != null) {
 //         this.getUsersCollection().document(uid).delete();
 //     }
 // }
}
