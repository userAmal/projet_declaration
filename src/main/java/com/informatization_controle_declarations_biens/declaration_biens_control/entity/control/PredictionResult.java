package com.informatization_controle_declarations_biens.declaration_biens_control.entity.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierNonBati;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.Vehicule;

public class PredictionResult {
    private Object entity; // Peut être FoncierBati ou Vehicule
    private double prediction;

    public PredictionResult(FoncierBati foncierBati, double prediction) {
        this.entity = foncierBati;
        this.prediction = prediction;
    }

    public PredictionResult(FoncierNonBati foncierNonBati, double prediction) {
        this.entity = foncierNonBati;
        this.prediction = prediction;
    }

    public PredictionResult(Vehicule vehicule, double prediction) {
        this.entity = vehicule;
        this.prediction = prediction;
    }

    public double getPrediction() {
        return prediction;
    }

    public FoncierBati getFoncierBati() {
        return entity instanceof FoncierBati ? (FoncierBati) entity : null;
    }

    public Vehicule getVehicule() {
        return entity instanceof Vehicule ? (Vehicule) entity : null;
    }

    public FoncierNonBati getFoncierNonBati() {
        return entity instanceof FoncierNonBati ? (FoncierNonBati) entity : null;
    }
}