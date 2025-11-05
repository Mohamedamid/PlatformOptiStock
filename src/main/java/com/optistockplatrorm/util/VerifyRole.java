package com.optistockplatrorm.util;

import com.optistockplatrorm.entity.Enums.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyRole {

    @Autowired
    private HttpSession session;

    public void checkAccess(String action) {
        Object roleObj = session.getAttribute("role");

        if (roleObj == null) {
            throw new RuntimeException("Utilisateur non connecté.");
        }

        Role role = (Role) roleObj;

        switch (action) {
            case "READ":
                if (role != Role.CLIENT && role != Role.ADMIN && role != Role.WAREHOUSE_MANAGER) {
                    throw new RuntimeException("Accès refusé : seuls les administrateurs ou les gestionnaires d’entrepôt peuvent " +
                            "effectuer cette action.");
                }
            case "CREATE":
                if (role != Role.ADMIN && role != Role.WAREHOUSE_MANAGER) {
                    throw new RuntimeException("Accès refusé : seuls les administrateurs ou les gestionnaires d’entrepôt peuvent " +
                            "effectuer cette action.");
                }
                break;

            case "UPDATE":
                if (role != Role.ADMIN) {
                    throw new RuntimeException("Accès refusé : seul un administrateur peut modifier un produit.");
                }
                break;

            case "DELETE":
                if (role != Role.ADMIN) {
                    throw new RuntimeException("Accès refusé : seul un administrateur peut supprimer un produit.");
                }
                break;

            default:
                throw new RuntimeException("Action non autorisée.");
        }
    }
}
