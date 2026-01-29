package fr.smeal.utils;

import java.util.List;

// Cette interface permet de renvoyer les données à la Vue quand elles sont prêtes
public interface FirestoreCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}