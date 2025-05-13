package com.informatization_controle_declarations_biens.declaration_biens_control.entity.control;

import com.informatization_controle_declarations_biens.declaration_biens_control.entity.declaration.FoncierBati;

public class PredictionResult {
    private FoncierBati foncierBati;
    private double prediction;

    public PredictionResult(FoncierBati foncierBati, double prediction) {
        this.foncierBati = foncierBati;
        this.prediction = prediction;
    }

    public FoncierBati getFoncierBati() {
        return foncierBati;
    }

    public double getPrediction() {
        return prediction;
    }
}
