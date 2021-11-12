package com.kardabel.go4lunch.usecase;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kardabel.go4lunch.model.UserModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FirestoreUseCase {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_DAYS = LocalDate.now().toString();
    private static final String COLLECTION_RESTAURANT_GOT_USERS = "favorite users";
    private static final String COLLECTION_USER_GOT_RESTAURANT = "users restaurant";
    private static final String USERNAME_FIELD = "username";

    private static volatile FirestoreUseCase instance;

    public FirestoreUseCase() {
    }

    public static FirestoreUseCase getInstance() {
        FirestoreUseCase result = instance;
        if (result != null) {
            return result;
        }
        synchronized (FirestoreUseCase.class) {
            if (instance == null) {
                instance = new FirestoreUseCase();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    // Get the Collection Reference
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // CREATE USER IN FIRESTORE
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String userUrlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String userName = user.getDisplayName();
            String uid = user.getUid();
            String userEmail = user.getEmail();

            UserModel userToCreate = new UserModel(uid, userName, userUrlPicture, userEmail);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    // GET USER DATA FROM FIRESTORE
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    // UPDATE USERS NAME
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }

    // DELETE USER FROM FIRESTORE
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }


    ///// FAVORITES RESTAURANTS //////

    public static CollectionReference getDayCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_DAYS);
    }

    // WHEN A CLICK IS PERFORMED ON FAVORITES DETAILS BUTTON, CHECK IF USER ALREADY EXIST ON DAYS DB
    public void onFavoriteClick(String placeID, String name) {
        String userId = this.getCurrentUserUID();

//      FirebaseFirestore.getInstance().collection(COLLECTION_DAYS)
//              .get()
//              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                  @Override
//                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                      if (task.isSuccessful()) {
//                          for (QueryDocumentSnapshot document : task.getResult()) {

//                              Log.d(TAG, document.getId() + " => " + document.getData());
//                          }
//                      } else {
//                          Log.d(TAG, "Error getting documents: ", task.getException());
//                      }
//                  }
//              });

        DocumentReference docIdRef = getDayCollection().document(placeID).collection(COLLECTION_RESTAURANT_GOT_USERS).document(userId);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        deleteUserInFavorite(placeID, userId);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        createFavorite(placeID, name);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void deleteUserInFavorite(String placeId, String userId){
        getDayCollection().document(placeId).collection(COLLECTION_RESTAURANT_GOT_USERS).document(userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        getDayCollection().document(placeId).collection(COLLECTION_USER_GOT_RESTAURANT).document(userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void createFavorite(String placeId, String restaurantName) {
        String userId = this.getCurrentUserUID();

        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put("restaurant name", restaurantName);
        restaurant.put("restaurant id", placeId);

        Map<String, Object> user = new HashMap<>();
        user.put("user id", userId);

        FirestoreUseCase
                .getDayCollection()
                .document(placeId)
                .set(restaurant);

        FirestoreUseCase
                .getDayCollection()
                .document(placeId)
                .collection(COLLECTION_RESTAURANT_GOT_USERS)
                .document(userId)
                .set(user);

        FirestoreUseCase
                .getDayCollection()
                .document(placeId)
                .collection(COLLECTION_USER_GOT_RESTAURANT)
                .document(userId)
                .set(restaurant);

    }
}
