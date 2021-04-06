package com.example.rocketapp.controller;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.trials.IntCountTrial;
import com.example.rocketapp.model.trials.MeasurementTrial;
import com.example.rocketapp.model.trials.Trial;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.example.rocketapp.controller.FirestoreDocument.readFirebaseObjectSnapshot;

/**
 * Class that handles adding, retrieving, and modifying experiment trials from firestore.
 */
public class TrialManager {
    private static final String TAG = "TrialManager";
    private static ListenerRegistration trialsListener;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String EXPERIMENTS = "Experiments";
    private static final String BARCODES = "Barcodes";
    private static final String TRIALS = "Trials";
    private static final ArrayList<Barcode> barCodes = new ArrayList<>();

    // TODO Add any new Trial types to this map
    static final ImmutableMap<String, Class<? extends Trial>> trialClassMap = ImmutableMap.<String, Class<? extends Trial>>builder()
            .put(IntCountTrial.TYPE, IntCountTrial.class)
            .put(MeasurementTrial.TYPE, MeasurementTrial.class)
            .put(BinomialTrial.TYPE, BinomialTrial.class)
            .put(CountTrial.TYPE, CountTrial.class)
            .build();

    /**
     * Private constructor, should not be instantiated
     */
    private TrialManager() {}

    /**
     * Listens to firestore for changes to this experiment. You MUST use this to get the trials for an experiment.
     * You may want to update the UI in onUpdate.
     * @param experiment
     *      The experiment to listen to.
     * @param onUpdate
     *      Callback for implementing desired behaviour when the experiment is updated in firestore.
     */
    public static void listen(Experiment<?> experiment, ObjectCallback<Experiment<?>> onUpdate) {
        if (!experiment.isValid()) {
            Log.e(TAG, "Cannot listen to an experiment without an id.");
            return;
        }

        // Listen for changes in Trials
        CollectionReference trialsRef = db.collection(EXPERIMENTS).document(experiment.getId().getKey()).collection(TRIALS);
        if (trialsListener != null) trialsListener.remove();
        trialsListener = trialsRef.addSnapshotListener((snapshots, e) -> {
            parseTrialsSnapshot(experiment, snapshots);
            Log.d(TAG, "Experiment Trials Updated: " + experiment.getTrials().size());
            onUpdate.callBack(experiment);
        });
    }

    public static class Barcode {

        private FirestoreDocument.Id experimentId;
        private Trial trial;
        private String code;

        public Barcode() { }

        public Barcode(String barcode, Experiment<?> experiment, Trial trial) {
            this.code = barcode;
            this.experimentId = experiment.getId();
            this.trial = trial;
        }

        public FirestoreDocument.Id getExperimentId() {
            return experimentId;
        }

        public Trial getTrial() {
            return trial;
        }

        public String getCode() {
            return code;
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
        registerBarcode(new Barcode(code, experiment, trial), onComplete, onFailure);
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


    public static void createQRCodeBitmap(Experiment<?> experiment, Trial trial, ObjectCallback<Bitmap> bitmap, ObjectCallback<String> generatedString, ObjectCallback<Exception> onFailure) {

        String code = experiment.getId().getKey() + " " + trial.getType() + " " + trial.getValueString();

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            //Initialize bit matrix
            BitMatrix matrix = writer.encode(code, BarcodeFormat.QR_CODE, 800,800);
            //Initialize barcode encoder
            BarcodeEncoder encoder = new BarcodeEncoder();

            // Return string representation
            generatedString.callBack(code);

            //Return bitmap
            bitmap.callBack(encoder.createBitmap(matrix));

        } catch (WriterException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            onFailure.callBack(e);
        }
    }

    private static Trial createTrial(String type, String value) {
        switch(type) {
            case BinomialTrial.TYPE:
                return new BinomialTrial(value.equals("true"));
            case IntCountTrial.TYPE:
                return new IntCountTrial(Integer.parseInt(value));
            case CountTrial.TYPE:
                return new CountTrial(Integer.parseInt(value));
            case MeasurementTrial.TYPE:
                return new MeasurementTrial(Float.parseFloat(value));
            default:
                return null;
        }

    }

    public static void readQRCode(String code, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {

        String[] data = code.split(" ");

        if (data.length != 3) {
            Log.e(TAG, "Invalid code: " + code);
            onFailure.callBack(new Exception("Invalid code: " + code));
            return;
        }

        Experiment<?> experiment = ExperimentManager.getExperiment(new FirestoreDocument.Id(data[0]));
        Trial trial = createTrial(data[1], data[2]);
        if (trial == null) {
            Log.e(TAG, "Invalid code: " + code);
            onFailure.callBack(new Exception("Invalid code: " + code));
            return;
        }

        ((FirestoreDocument)trial).newTimestamp();
        addTrial(trial, experiment, onComplete, onFailure);
    }


    /**
     * Reads a barcode and adds the corresponding trial if it exists in the registry
     * @param code the barcode being read
     * @param onComplete
     * @param onFailure
     */
    public static void readBarcode(String code, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {

        CollectionReference barCodesRef = db.collection(BARCODES);

        barCodesRef.document(code).get().addOnSuccessListener(snapshot -> {

            String trialType = (String) ((Map<String, Object>)snapshot.getData().get("trial")).get("type");
            Experiment<?> experiment = ExperimentManager.getExperiment(snapshot.get("experimentId", FirestoreDocument.Id.class));

            Barcode barcode = new Barcode(
                    snapshot.getString("code"),
                    experiment,
                    snapshot.get("trial", trialClassMap.get(trialType))
            );

            ((FirestoreDocument) barcode.trial).newTimestamp();
            addTrial(barcode.trial, experiment, onComplete, onFailure);

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to read barcode.");
            onFailure.callBack(e);
        });

    }


    /**
     * Add a new trial for an experiment.
     * @param trial
     *      New trial to add.
     * @param experiment
     *      Experiment to add trial to.
     * @param onSuccess
     *      Callback for when push is successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void addTrial(Trial trial, Experiment<?> experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        push(trial, experiment, onSuccess, onFailure);
    }

    /**
     * Update a trial for an experiment.
     * @param trial
     *      New trial to add.
     * @param experiment
     *      Experiment to add trial to.
     * @param onSuccess
     *      Callback for when push is successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void update(Trial trial, Experiment<?> experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        push(trial, experiment, onSuccess, onFailure);
    }

    /**
     * Add or modify a trial on firestore
     * @param trial
     *      The trial to add/modify
     * @param experiment
     *      The experiment to add the trial to
     * @param onComplete
     *      Callback for when trial is added/modified successfully
     * @param onFailure
     *      Callback for when trial push fails
     */
    private static void push(Trial trial, Experiment<?> experiment, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {
        if (!UserManager.isSignedIn()) {
            Log.e(TAG, "Push failed. Must be logged in to push.");
            onFailure.callBack(new Exception("Push failed. Must be logged in to push."));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Push failed. Tried to add trial to experiment without ID.");
            onFailure.callBack(new Exception("Push failed. Tried to add trial to experiment without ID."));
            return;
        }

        if (trial == null) {
            Log.e(TAG, "Push failed. Tried to add null trial to experiment.");
            onFailure.callBack(new Exception("Push failed. Tried to add null trial to experiment."));
            return;
        }

        if (trial.ownerIsValid() && !UserManager.getUser().isOwner(experiment)) {
            Log.e(TAG, "Push failed. User does not own experiment. Cannot update trial.");
            onFailure.callBack(new Exception("Push failed. User does not own experiment. Cannot update trial."));
            return;
        }

        CollectionReference trialsRef = db.collection(EXPERIMENTS).document(experiment.getId().getKey()).collection(TRIALS);

        if (trial.getId() != null) {
            trialsRef.document(trial.getId().getKey()).set(trial).addOnSuccessListener(trialSnapshot -> {
                Log.d(TAG, "Trial Updated.");
                onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to update Trial.");
                onFailure.callBack(e);
            });
        } else {
            ((FirestoreOwnableDocument) trial).setOwner(UserManager.getUser());
            ((FirestoreNestableDocument) trial).setParent(experiment.getId());

            trialsRef.add(trial).addOnSuccessListener(trialSnapshot -> {
                ((FirestoreDocument) trial).setId(new FirestoreDocument.Id(trialSnapshot.getId()));
                Log.d(TAG, "Trial Added.");
                onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to add Trial.");
                onFailure.callBack(e);
            });
        }
    }

    /**
     * Parses a trials list snapshot from firestore and adds them to an experiment.
     * @param experiment
     *      The experiment the trials belong to
     * @param userSnapshots
     *      The snapshot from firestore to parse
     */
    private static <TrialType extends Trial> void parseTrialsSnapshot(Experiment<TrialType> experiment, QuerySnapshot userSnapshots) {
        ArrayList<TrialType> array = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots) {
            Class<? extends Trial> classType = trialClassMap.get(snapshot.getString("type"));
            if (classType != null) array.add((TrialType) readFirebaseObjectSnapshot(classType, snapshot, TAG));
            else Log.e(TAG, "classType null in parseTrialsSnapshot.");
        }

        experiment.setTrials(array);
    }
}
