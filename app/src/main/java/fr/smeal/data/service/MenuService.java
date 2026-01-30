package fr.smeal.data.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Menu;
import fr.smeal.data.repository.MenuRepository;

public class MenuService {

    private final MenuRepository repository;

    public MenuService() {
        this.repository = new MenuRepository();
    }

    public Task<Void> creerMenuRestaurant(String uid, Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Le menu ne peut pas être null");
        }
        if (uid == null || uid.isEmpty()) {
            throw new IllegalArgumentException("L'ID menu est invalide");
        }

        return repository.saveMenu(uid, menu);
    }

    public Task<Menu> getMenuRestaurant(String uid) {
        return repository.getMenu(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Menu.class);
                }
            }
            return null;
        });
    }
}