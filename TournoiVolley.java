package com.example.tournoivolley;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Application JavaFX pour la gestion d'inscriptions d'un tournoi de volley-ball
 */
public class TournoiVolley extends Application {

    // Constantes
    private static final int MAX_EQUIPES = 8;
    private static final int JOUEURS_PAR_EQUIPE = 6;
    private static final LocalDate DATE_DEBUT_INSCRIPTIONS = LocalDate.of(2025, 5, 9);
    private static final LocalDate DATE_FIN_INSCRIPTIONS = LocalDate.of(2025, 5, 16);

    // Données
    private ObservableList<Equipe> equipes;
    private Set<String> joueursInscrits;
    private LocalDate dateActuelle;

    // Composants GUI
    private ListView<Equipe> listViewEquipes;
    private Label labelStatut;
    private TextField[] textFieldsJoueurs;
    private TextField textFieldNomEquipe;
    private DatePicker datePickerTest;

    /**
     * Classe représentant une équipe de volley-ball
     */
    public static class Equipe {
        private String nom;
        private ArrayList<String> joueurs;

        public Equipe(String nom) {
            this.nom = nom;
            this.joueurs = new ArrayList<>();
        }

        public String getNom() {
            return this.nom;
        }

        public ArrayList<String> getJoueurs() {
            return this.joueurs;
        }

        public void ajouterJoueur(String joueur) {
            this.joueurs.add(joueur);
        }

        @Override
        public String toString() {
            return nom + " (" + joueurs.size() + " joueurs)";
        }

        public String getDetailsJoueurs() {
            return String.join(", ", joueurs);
        }
    }

    /**
     * Exception personnalisée pour les erreurs d'inscription
     */
    public class InscriptionException extends Exception {
        public InscriptionException(String message) {
            super(message);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialisation des données
        equipes = FXCollections.observableArrayList();
        joueursInscrits = new HashSet<>();
        dateActuelle = LocalDate.now();

        // Configuration de l'interface
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Panneau d'information en haut
        VBox topPanel = createTopPanel();
        root.setTop(topPanel);

        // Liste des équipes à gauche
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);

        // Formulaire d'inscription à droite
        VBox rightPanel = createRightPanel();
        root.setRight(rightPanel);

        // Mise en page finale
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Gestion des inscriptions - Tournoi de Volley-ball");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Mise à jour initiale de l'interface
        updateStatusLabel();
    }

    /**
     * Crée le panneau d'information en haut
     */
    private VBox createTopPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        Label labelTitle = new Label("TOURNOI DE VOLLEY-BALL");
        labelTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox dateBox = new HBox(10);
        Label labelDate = new Label("Date actuelle:");
        datePickerTest = new DatePicker(dateActuelle);
        datePickerTest.setOnAction(e -> {
            dateActuelle = datePickerTest.getValue();
            updateStatusLabel();
        });

        Button btnRefresh = new Button("Actualiser");
        btnRefresh.setOnAction(e -> updateStatusLabel());

        dateBox.getChildren().addAll(labelDate, datePickerTest, btnRefresh);

        labelStatut = new Label();
        labelStatut.setStyle("-fx-font-style: italic;");

        panel.getChildren().addAll(labelTitle, dateBox, labelStatut);
        return panel;
    }

    /**
     * Crée le panneau gauche avec la liste des équipes
     */
    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(400);

        Label labelListe = new Label("Équipes inscrites:");
        labelListe.setStyle("-fx-font-weight: bold;");

        listViewEquipes = new ListView<>(equipes);
        listViewEquipes.setPrefHeight(400);

        // Personnalisation de l'affichage des équipes
        listViewEquipes.setCellFactory(new Callback<ListView<Equipe>, ListCell<Equipe>>() {
            @Override
            public ListCell<Equipe> call(ListView<Equipe> param) {
                return new ListCell<Equipe>() {
                    @Override
                    protected void updateItem(Equipe equipe, boolean empty) {
                        super.updateItem(equipe, empty);
                        if (empty || equipe == null) {
                            setText(null);
                        } else {
                            setText(equipe.getNom());
                            setTooltip(new Tooltip("Joueurs: " + equipe.getDetailsJoueurs()));
                        }
                    }
                };
            }
        });

        // Affichage détaillé quand une équipe est sélectionnée
        TextArea textAreaDetails = new TextArea();
        textAreaDetails.setEditable(false);
        textAreaDetails.setPrefHeight(150);

        listViewEquipes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Équipe: ").append(newVal.getNom()).append("\n\n");
                sb.append("Joueurs:\n");

                ArrayList<String> joueurs = newVal.getJoueurs();
                for (int i = 0; i < joueurs.size(); i++) {
                    sb.append(i + 1).append(". ").append(joueurs.get(i)).append("\n");
                }

                textAreaDetails.setText(sb.toString());
            } else {
                textAreaDetails.clear();
            }
        });

        panel.getChildren().addAll(labelListe, listViewEquipes, textAreaDetails);
        return panel;
    }

    /**
     * Crée le panneau droit avec le formulaire d'inscription
     */
    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(400);

        Label labelInscription = new Label("Inscription d'une nouvelle équipe:");
        labelInscription.setStyle("-fx-font-weight: bold;");

        // Nom de l'équipe
        HBox nomEquipeBox = new HBox (10);
        Label labelNomEquipe = new Label("Nom de l'équipe:");
        labelNomEquipe.setPrefWidth(100);
        textFieldNomEquipe = new TextField();
        textFieldNomEquipe.setPrefWidth(250);
        nomEquipeBox.getChildren().addAll(labelNomEquipe, textFieldNomEquipe);

        // Joueurs
        VBox joueursBox = new VBox(5);
        Label labelJoueurs = new Label("Joueurs:");
        labelJoueurs.setStyle("-fx-font-weight: bold;");
        joueursBox.getChildren().add(labelJoueurs);

        textFieldsJoueurs = new TextField[JOUEURS_PAR_EQUIPE];
        for (int i = 0; i < JOUEURS_PAR_EQUIPE; i++) {
            HBox joueurBox = new HBox(10);
            Label labelJoueur = new Label("Joueur " + (i + 1) + ":");
            labelJoueur.setPrefWidth(70);
            textFieldsJoueurs[i] = new TextField();
            textFieldsJoueurs[i].setPrefWidth(280);
            joueurBox.getChildren().addAll(labelJoueur, textFieldsJoueurs[i]);
            joueursBox.getChildren().add(joueurBox);
        }

        // Boutons
        HBox buttonsBox = new HBox(10);
        Button btnInscrire = new Button("Inscrire l'équipe");
        btnInscrire.setPrefWidth(200);
        Button btnReset = new Button("Réinitialiser");
        btnReset.setPrefWidth(150);

        // Action pour inscrire une équipe
        btnInscrire.setOnAction(e -> {
            try {
                inscrireEquipe();
            } catch (InscriptionException ex) {
                showAlert("Erreur d'inscription", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Action pour réinitialiser le formulaire
        btnReset.setOnAction(e -> {
            textFieldNomEquipe.clear();
            for (TextField tf : textFieldsJoueurs) {
                tf.clear();
            }
        });

        buttonsBox.getChildren().addAll(btnInscrire, btnReset);

        panel.getChildren().addAll(labelInscription, nomEquipeBox, joueursBox, buttonsBox);
        return panel;
    }

    /**
     * Met à jour le label de statut
     */
    private void updateStatusLabel() {
        StringBuilder status = new StringBuilder();

        // Période d'inscription
        status.append("Inscriptions: ");
        if (inscriptionsOuvertes()) {
            status.append("OUVERTES");
        } else {
            status.append("FERMÉES");
        }

        status.append(" (").append(DATE_DEBUT_INSCRIPTIONS.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append(" au ").append(DATE_FIN_INSCRIPTIONS.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append(") | ");

        // Nombre d'équipes
        status.append("Équipes: ").append(equipes.size()).append("/").append(MAX_EQUIPES);

        labelStatut.setText(status.toString());
    }

    /**
     * Vérifie si les inscriptions sont ouvertes
     */
    private boolean inscriptionsOuvertes() {
        return !dateActuelle.isBefore(DATE_DEBUT_INSCRIPTIONS) &&
                !dateActuelle.isAfter(DATE_FIN_INSCRIPTIONS);
    }

    /**
     * Inscrit une nouvelle équipe
     */
    private void inscrireEquipe() throws InscriptionException {
        if (!inscriptionsOuvertes()) {
            throw new InscriptionException("Les inscriptions sont fermées.\nPériode d'inscription: " +
                    DATE_DEBUT_INSCRIPTIONS.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                    " au " + DATE_FIN_INSCRIPTIONS.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (equipes.size() >= MAX_EQUIPES) {
            throw new InscriptionException("Le nombre maximum d'équipes (" + MAX_EQUIPES + ") est atteint.");
        }

        String nomEquipe = textFieldNomEquipe.getText().trim();
        if (nomEquipe.isEmpty()) {
            throw new InscriptionException("Le nom de l'équipe ne peut pas être vide.");
        }

        // Vérifier si le nom d'équipe existe déjà
        for (Equipe e : equipes) {
            if (e.getNom().equalsIgnoreCase(nomEquipe)) {
                throw new InscriptionException("Une équipe avec ce nom existe déjà.");
            }
        }

        Equipe nouvelleEquipe = new Equipe(nomEquipe);
        ArrayList<String> nouveauxJoueurs = new ArrayList<>();

        // Vérifier les noms des joueurs
        for (int i = 0; i < JOUEURS_PAR_EQUIPE; i++) {
            String nomJoueur = textFieldsJoueurs[i].getText().trim();

            if (nomJoueur.isEmpty()) {
                throw new InscriptionException("Le nom du joueur " + (i + 1) + " ne peut pas être vide.");
            }

            if (joueursInscrits.contains(nomJoueur.toLowerCase())) {
                throw new InscriptionException("Le joueur '" + nomJoueur + "' est déjà inscrit dans une autre équipe.");
            }

            nouveauxJoueurs.add(nomJoueur);
        }

        // Tout est valide, on peut inscrire l'équipe
        for (String joueur : nouveauxJoueurs) {
            nouvelleEquipe.ajouterJoueur(joueur);
            joueursInscrits.add(joueur.toLowerCase());
        }

        equipes.add(nouvelleEquipe);
        updateStatusLabel();

        // Réinitialiser le formulaire
        textFieldNomEquipe.clear();
        for (TextField tf : textFieldsJoueurs) {
            tf.clear();
        }

        showAlert("Succès", "L'équipe '" + nomEquipe + "' a été inscrite avec succès!", Alert.AlertType.INFORMATION);
    }

    /**
     * Affiche une boîte de dialogue
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Point d'entrée du programme
     */
    public static void main(String[] args) {
        launch(args);
    }
}