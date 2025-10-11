package com.example.CdnOriginServer.service;

import com.example.CdnOriginServer.component.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateCachesService {
    private final Helper helper;

    @Autowired
    public UpdateCachesService(Helper helper) { this.helper = helper; }

    // Ενημέρωση διακομιστών κρυφής μνήμης.
    // Η αποτυχία ενημέρωσής τους δεν επηρεάζει τον κεντρικό διακομιστή.
    public String updateCaches(String filename) {
        String responseEdge1 = helper.informEdge1(filename);
        String responseEdge2 = helper.informEdge2(filename);

        String response = "";
        if (!responseEdge1.contains("Failed") && !responseEdge2.contains("Failed")) {
            response = responseEdge1 + "1, " + responseEdge2 + "2 ";
        } else if (responseEdge1.contains("Failed") && !responseEdge2.contains("Failed")) {
            response = responseEdge1 + "but " + responseEdge2 + "2 ";
        } else if (!responseEdge1.contains("Failed") && responseEdge2.contains("Failed")){
            response = responseEdge1 + "1 but" + responseEdge2;
        } else {
            response = responseEdge1 + " " + responseEdge2 + " ";
        }

        return response;
    }
}