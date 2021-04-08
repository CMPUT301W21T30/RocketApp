package com.example.rocketapp.controller;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Map;
import java.util.Objects;

public class ScannerManager {
    private static final String TAG = "ScannerManager";
    private static final String EXPERIMENTS = "Experiments";
    private static final String BARCODES = "Barcodes";
    private static final String TRIALS = "Trials";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Represents a barcode and its associated experiment and trial. Stored in firestore when a barcode is registered.
     */
    public static class Barcode {

        private FirestoreDocument.Id experimentId;
        private Trial trial;
        private String code;

        /**
         * Default constructor only used for interface with Firestore.
         */
        public Barcode() { }

        /**
         * Creates a barcode corresponding to an experiment trial to be stored in firestore.
         * @param barcode read barcode
         * @param experimentId id of registered experiment
         * @param trial registered trial
         */
        public Barcode(String barcode, FirestoreDocument.Id experimentId, Trial trial) {
            this.code = barcode;
            this.experimentId = experimentId;
            this.trial = trial;
        }

        /**
         * @return returns the documentId for the corresponding experiment
         */
        public FirestoreDocument.Id getExperimentId() {
            return experimentId;
        }

        /**
         * @return trial associated with this barcode
         */
        public Trial getTrial() {
            return trial;
        }
    }

    /**
     * Reads a barcode or QR code and adds the corresponding trial if code is valid
     * @param code the barcode being read
     * @param onComplete callback returning the trial that was added
     * @param onFailure callback for when read fails
     */
    public static void readCode(String code, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {
        switch(code.split(" ").length) {
            case 3:
                ScannerManager.processQRCode(code, onComplete, onFailure);
                break;
            case 1:
                ScannerManager.processBarcode(code, onComplete, onFailure);
                break;
            default:
                Log.d(TAG, "Code does not correspond to an experiment: " + code);
                onFailure.callBack(new Exception("Code does not correspond to an experiment."));
        }
    }


    /**
     * Registers a barcode to be used for uploading trials to an experiment
     * @param code the barcode
     * @param experiment experiment to add a trial for
     * @param trial the trial to add
     * @param onComplete callback for when registration is complete
     * @param onFailure callback for when registration fails
     */
    public static void registerBarcode(String code, Experiment<?> experiment, Trial trial, ObjectCallback<Barcode> onComplete, ObjectCallback<Exception> onFailure) {
        registerBarcode(new Barcode(code, experiment.getId(), trial), onComplete, onFailure);
    }


    /**
     * Registers a barcode to be used for uploading trials to an experiment
     * @param barcode the barcode to register
     * @param onComplete callback for when registration is complete
     * @param onFailure callback for when registration fails
     */
    public static void registerBarcode(Barcode barcode, ObjectCallback<Barcode> onComplete, ObjectCallback<Exception> onFailure) {
        CollectionReference barCodesRef = db.collection(BARCODES);

        barCodesRef.document(barcode.code).set(barcode).addOnSuccessListener(trialSnapshot -> {
            Log.d(TAG, "Barcode registered.");
            onComplete.callBack(barcode);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to register barcode.");
            onFailure.callBack(e);
        });
    }


    /**
     * Generates a bitmap representing a trial for an experiment.
     * @param experiment experiment trial is for
     * @param trial trial to generate bitmap for
     * @param bitmapCallback callback returning the generated bitmap
     * @param generatedString callback returning the string representation of the bitmap
     * @param onFailure callback for when generating bitmap fails
     */
    public static void createQRCodeBitmap(Experiment<?> experiment, Trial trial, ObjectCallback<Bitmap> bitmapCallback, ObjectCallback<String> generatedString, ObjectCallback<Exception> onFailure) {
        String code = experiment.getId().getKey() + " " + trial.getType() + " " + trial.getValueString();
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(code, BarcodeFormat.QR_CODE, 800,800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            generatedString.callBack(code);
            bitmapCallback.callBack(encoder.createBitmap(matrix));
        } catch (WriterException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            onFailure.callBack(e);
        }
    }


    /**
     * Reads a qrCode and adds the corresponding trial
     * @param code the barcode being read
     * @param onComplete callback returning the trial that was added
     * @param onFailure callback for when read fails
     */
    public static void processQRCode(String code, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {
        String failMessage = "Invalid code: " + code;

        if (code == null) {
            Log.e(TAG, failMessage);
            onFailure.callBack(new Exception(failMessage));
            return;
        }

        String[] data = code.split(" ");

        if (data.length != 3) {
            Log.e(TAG, failMessage);
            onFailure.callBack(new Exception(failMessage));
            return;
        }

        Experiment<?> experiment = ExperimentManager.getExperiment(new FirestoreDocument.Id(data[0]));
        Trial trial = TrialManager.createTrial(data[1], data[2]);
        if (experiment == null || trial == null) {
            Log.e(TAG, failMessage);
            onFailure.callBack(new Exception(failMessage));
            return;
        }

        ((FirestoreDocument)trial).newTimestamp();
        TrialManager.addTrial(trial, experiment, onComplete, onFailure);
    }


    /**
     * Reads a barcode and returns registered barcode information if it is registered
     * @param code the barcode being read
     * @param onComplete
     * @param onFailure
     */
    public static void readBarcode(String code, ObjectCallback<Barcode> onComplete, ObjectCallback<Exception> onFailure) {
        String failMessage = "Barcode not registered to experiment.";

        db.collection(BARCODES).document(code).get().addOnSuccessListener(snapshot -> {

            if (!snapshot.exists()) {
                Log.e(TAG, failMessage);
                onFailure.callBack(new Exception(failMessage));
                return;
            }

            String trialType = (String) ((Map<String, Object>) snapshot.getData().get("trial")).get("type");

            Barcode barcode = new Barcode(
                    snapshot.getString("code"),
                    snapshot.get("experimentId", FirestoreDocument.Id.class),
                    snapshot.get("trial", TrialManager.trialClassMap.get(trialType))
            );

            if (ExperimentManager.getExperiment(barcode.getExperimentId()) == null) {
                Log.e(TAG, failMessage);
                onFailure.callBack(new Exception(failMessage));
            } else {
                onComplete.callBack(barcode);
            }

        }).addOnFailureListener(e -> {
            Log.e(TAG, failMessage);
            onFailure.callBack(e);
        });
    }


    /**
     * Reads a barcode and adds the corresponding trial if it exists in the registry
     * @param code the barcode being read
     * @param onComplete
     * @param onFailure
     */
    public static void processBarcode(String code, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {
        readBarcode(code, barcode->{
            Experiment<?> experiment = ExperimentManager.getExperiment(barcode.getExperimentId());
            ((FirestoreDocument) barcode.trial).newTimestamp();
            TrialManager.addTrial(barcode.trial, experiment, onComplete, onFailure);
        }, onFailure);
    }


}
