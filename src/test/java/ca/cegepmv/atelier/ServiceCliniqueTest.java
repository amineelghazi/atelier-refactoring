package ca.cegepmv.atelier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Atelier — Réusinage (refactoring)
 * =================================
 *
 * Cette suite de tests est VOLONTAIREMENT INCOMPLÈTE. Les 4 tests ci-dessous passent
 * déjà et couvrent seulement une partie du comportement de {@link ServiceClinique}.
 *
 * Avant de réusiner quoi que ce soit (voir README, Partie 3), complétez cette classe
 * (Partie 2) avec des tests de CARACTÉRISATION qui documentent le comportement ACTUEL
 * du code (pas celui que vous jugeriez "idéal") pour :
 *
 *   1. genererFacture() — le cas VACCIN, le cas où le rabais de fidélité s'applique
 *      (2 animaux ou plus), et le cas de pénalité de solde négatif.
 *   2. calculerRemiseFidelite() — les 3 branches non couvertes (0.15, 0.10, 0.05).
 *   3. estimerCoutAnnuel() — au moins un cas avec plusieurs visites de types différents.
 *   4. calculerDureeRendezVous() — au moins les 4 types (CONSULTATION, VACCIN,
 *      CHIRURGIE, type inconnu).
 *   5. genererRapportPourVeterinaire() — au moins un cas normal.
 *   6. planifierVisite() — le cas avec rappel par courriel activé ET désactivé
 *      (vérifiez le contenu de getJournalRappels()).
 *
 * N'ajoutez PAS de test pour un comportement que vous jugez incorrect (ex. le tarif
 * silencieux à 0$ pour un type inconnu) sans d'abord relire l'avertissement dans
 * ServiceClinique — ce comportement est un piège intentionnel de l'atelier.
 */
class ServiceCliniqueTest {

    private ServiceClinique service;
    private Proprietaire julie;

    @BeforeEach
    void setUp() {
        service = new ServiceClinique();
        // Arrange commun : propriétaire avec un seul animal, solde à jour, cliente depuis 1 an
        julie = new Proprietaire("Julie", "Tremblay", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "julie.tremblay@example.com", "450-555-0100", 0.0, 1);
        julie.ajouterAnimal(new Animal(1, "Rex", "CHIEN", LocalDate.of(2020, 3, 15)));
    }

    // ------------------------------------------------------------------
    // Tests fournis (déjà complets, à titre d'exemple — NE PAS MODIFIER)
    // ------------------------------------------------------------------

    @Test
    void genererFacture_uneConsultationSansUrgence_calculeLeBonSousTotal() {
        // Arrange
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Gagnon", "Visite de routine");

        // Act
        String facture = service.genererFacture(julie, List.of(consultation));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $60.00 — Total: $60.00", facture);
    }

    @Test
    void genererFacture_chirurgieUrgente_appliqueLeMultiplicateurDurgence() {
        // Arrange
        Visite chirurgieUrgente = new Visite(LocalDate.now(), "CHIRURGIE", true, "Dr Gagnon", "Urgence");

        // Act
        String facture = service.genererFacture(julie, List.of(chirurgieUrgente));

        // Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $375.00 — Total: $375.00", facture);
    }

    @Test
    void genererFacture_sansVisite_lanceUneException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.genererFacture(julie, List.of()));
    }

    @Test
    void calculerRemiseFidelite_unSeulAnimalEtPeuAnciennete_retourneZero() {
        // Act
        double remise = service.calculerRemiseFidelite(julie);

        // Assert
        assertEquals(0.0, remise);
    }

    // ------------------------------------------------------------------
    // À COMPLÉTER — voir README, Partie 2 (tests de caractérisation)
    // ------------------------------------------------------------------

    // TODO 1 — genererFacture() : cas VACCIN (tarif de base 35$, sans urgence)
    @Test 
    void genererFacture_unVaccinSansUrgence_calculeLeBonSousTotal(){
        //Arrange
        Visite vaccin = new Visite(LocalDate.now(), "VACCIN", false,  "Dr Marc", "Vaccin annuel");

        //Act 
        String facture = service.genererFacture(julie, List.of(vaccin));

        //Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Total: $35.00", facture);
    }



    // TODO 2 — genererFacture() : propriétaire avec 2 animaux ou plus (rabais de 10%
    //          appliqué, et présent dans le texte de la facture — vérifiez le format
    //          exact affiché par le code actuel avant d'écrire votre assertion)
    @Test 
    void genererFacture_proprietaireDeuxAnimauxOuPlus_calculeLeBonSousTotal(){
        //Arrange
        julie.ajouterAnimal(new Animal(2, "Leo", "CHIEN", LocalDate.of(2022, 3, 15)));
        Visite vaccin = new Visite(LocalDate.now(), "VACCIN", false,  "Dr Marc", "Vaccin annuel");

        //Act
        String facture = service.genererFacture(julie, List.of(vaccin));

        //Assert
        assertEquals("Facture pour Julie Tremblay — Sous-total: $35.00 — Remise fidélité: -10% — Total: $31.50", facture);
    }

    // TODO 3 — genererFacture() : propriétaire avec un solde négatif (pénalité de 5%)
    @Test 
    void genererFacture_proprietaireSoldeNegatif_calculeLeBonSousTotal(){
        //Arrange
        Proprietaire Martin = new Proprietaire("Martin", "Tremblay", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "martin.tremblay@example.com", "450-555-0100", -10, 1);
        Martin.ajouterAnimal(new Animal(1, "Tiger", "CHIEN", LocalDate.of(2020, 3, 15)));
        Visite consultation = new Visite(LocalDate.now(), "CONSULTATION", false, "Dr Sacha", "Première Consultation");

        //Act
        String facture = service.genererFacture(Martin, List.of(consultation));

        //Assert
        assertEquals("Facture pour Martin Tremblay — Sous-total: $60.00 — Total: $63.00", facture);

    }
    // TODO 4 — calculerRemiseFidelite() : plus d'un animal ET anneesClient >= 3 (0.15)
    @Test 
    void calculerRemiseFidelite_plusUnAnimalEtAnneeClientTroisAnsOuPlus_retourne15Pourcent(){
        //Arrange
        Proprietaire joe = new Proprietaire("Joe", "Williams", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "joe.williams@example.com", "450-555-0100", 0.0, 3);
        joe.ajouterAnimal(new Animal(1, "Luna", "CHAT", LocalDate.of(2024, 5, 7)));
        joe.ajouterAnimal(new Animal(2, "Soleil", "CHAT", LocalDate.of(2023, 6, 9)));

        //Act
        double remise = service.calculerRemiseFidelite(joe);

        //Assert
        assertEquals(0.15, remise);
    }
    // TODO 5 — calculerRemiseFidelite() : plus d'un animal ET anneesClient < 3 (0.10)
    @Test 
    void calculerRemiseFidelite_plusUnAnimalEtAnneesClientMoinsDeTrois_retourne10Pourcent(){
        //Arrange
        Proprietaire jessica = new Proprietaire("Jessica", "Williams", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "jessica.williams@example.com", "450-555-0100", 0.0, 2);
        jessica.ajouterAnimal(new Animal(1, "Baron", "CHIEN", LocalDate.of(2021, 5, 7)));
        jessica.ajouterAnimal(new Animal(1, "Rufus", "CHIEN", LocalDate.of(2021, 5, 7)));

        //Act
        double remise = service.calculerRemiseFidelite(jessica);

        //Assert
        assertEquals(0.10, remise);
    }

    // TODO 6 — calculerRemiseFidelite() : un seul animal ET anneesClient >= 5 (0.05)
    @Test 
    void calculerRemiseFidelite_unSeulAnimalEtAnneesClientSuperieurOuEgalA5_retourne5Pourcent(){
        //Arrange
        Proprietaire sara = new Proprietaire("Sara", "Desjardins", "123 rue des Lilas", "Longueuil",
                "J4G 1A1", "sara.desjardins@example.com", "450-555-0100", 0.0, 6);
        sara.ajouterAnimal(new Animal(1, "Rocky", "CHIEN", LocalDate.of(2021, 5, 7)));
        
        //Act
        double remise = service.calculerRemiseFidelite(sara);

        //Assert
        assertEquals(0.05, remise);
    }

    // TODO 7 — estimerCoutAnnuel() : plusieurs visites de types différents
    @Test 
    void estimerCoutAnnuel_plusieurVisiteTypeDifferent_retourneLeBonCout(){
        //Arrange
        List<Visite> visites = new ArrayList<>();
        Visite visiteTypeConsultation =  new Visite(LocalDate.now(), "CONSULTATION", false,  "Dr Marc", "Consultation annuel");
        Visite visiteTypeVaccin =  new Visite(LocalDate.now(), "VACCIN", false,  "Dr Marc", "Vaccin annuel");
        Visite visiteTypeChirurgie =  new Visite(LocalDate.now(), "CHIRURGIE", false,  "Dr Marc", "Chirurgie annuel");
        visites.add(visiteTypeConsultation);
        visites.add(visiteTypeVaccin);
        visites.add(visiteTypeChirurgie);

        //Act
        double estimationTarifs = service.estimerCoutAnnuel(visites);

       //Assert
       assertEquals(4140, estimationTarifs);
    }
    // TODO 8 — calculerDureeRendezVous() : CONSULTATION, VACCIN, CHIRURGIE, et un type
    //          inconnu (documentez le comportement actuel, ne le "corrigez" pas)
    @Test 
    void calculerDureeRendezVous_consultationVaccinChirurgieTypeInconnue_retourneDureeTotal(){
        //Arrange
        Visite visiteTypeConsultation =  new Visite(LocalDate.now(), "CONSULTATION", false,  "Dr Marc", "Consultation annuel");
        Visite visiteTypeVaccin =  new Visite(LocalDate.now(), "VACCIN", false,  "Dr Marc", "Vaccin annuel");
        Visite visiteTypeChirurgie =  new Visite(LocalDate.now(), "CHIRURGIE", false,  "Dr Marc", "Chirurgie annuel");
        Visite visiteTypePhysiotherapie = new Visite(LocalDate.now(), "PHYSIOTHERAPIE", false,  "Dr Marc", "Soins de Physiothérapie");

        //Act
        int dureeConsultation = service.calculerDureeRendezVous(visiteTypeConsultation);
        int dureeVaccin = service.calculerDureeRendezVous(visiteTypeVaccin);
        int dureeChirurgie = service.calculerDureeRendezVous(visiteTypeChirurgie);
        int dureeTypeInconnue = service.calculerDureeRendezVous(visiteTypePhysiotherapie);

        //Assert
        assertEquals(20, dureeConsultation);
        assertEquals(10,  dureeVaccin);
        assertEquals(90, dureeChirurgie);
        assertEquals(30, dureeTypeInconnue);
    }
    // TODO 9 — genererRapportPourVeterinaire() : un cas normal avec un Veterinaire,
    //          un Proprietaire et une liste de visites
    @Test 
    void genererRapportPourVeterinaire_unVeterinaireUnProprietaireEtUneListeDeVisites_retourneRapportDuVeterinaire(){
        //Arrange
        Veterinaire joe = new Veterinaire("Joe", "Chirurgie");
        Proprietaire ash =  new Proprietaire("ash", "ketchum", "123 rue des Lilas", "montréal",
                "J4G 1A1", "ash.ketchum@example.com", "450-555-0100", 0.0, 3);
        List<Visite> visites = new ArrayList<>();
        Visite visiteTypeConsultation =  new Visite(LocalDate.now(), "CONSULTATION", false,  "Dr Marc", "Consultation annuel");
        Visite visiteTypeVaccin =  new Visite(LocalDate.now(), "VACCIN", false,  "Dr Marc", "Vaccin annuel");
        visites.add(visiteTypeConsultation);
        visites.add(visiteTypeVaccin);

        //Act
        String rapportVeterinaire = service.genererRapportPourVeterinaire(joe, ash, visites);

        //Assert
        assertEquals("Rapport du Dr Joe (Chirurgie)\nClient: ash ketchum\nAdresse: 123 rue des Lilas, montréal J4G 1A1\nContact: ash.ketchum@example.com / 450-555-0100\nNombre de visites: 2", rapportVeterinaire);
    }
    // TODO 10 — planifierVisite() : un appel avec envoyerRappelEmail=true (vérifiez le
    //           contenu de service.getJournalRappels()) ET un appel avec
    //           envoyerRappelEmail=false (le journal doit rester vide)
    // @Test
    // void planifierVisite_rappelTrueEtFalse_seulLeRappelTrueEcritDansLeJournal(){
    //     //Act rappel true
    //     service.planifierVisite(1, LocalDate.of(2026,11,17), "CONSULTATION", false, "Dr Louise", "Premier rendez-vous pour luna", true);

    //     //Assert rappel true
    //     assertEquals(1, service.getJournalRappels().size());
    //     assertEquals("Rappel envoyé pour la visite du 2026-11-17 (animal #1)", service.getJournalRappels().get(0));

    //     //Act rappel false
    //     service.planifierVisite(2, LocalDate.of(2027, 3,1), "VACCIN", false, "Dr Léa", "Vaccin pour Rock", false);

    //     //Assert rappel false
    //     assertEquals(1, service.getJournalRappels().size());
    // }

    @Test 
    void planifierVisite_rappelTrue_doitEcrireDansLeJournal(){
        //Act + assert
        service.planifierVisite(1, LocalDate.of(2026,11,17), "CONSULTATION", false, "Dr Louise", "Premier rendez-vous pour luna", true);
        assertEquals(1, service.getJournalRappels().size());
        assertEquals("Rappel envoyé pour la visite du 2026-11-17 (animal #1)", service.getJournalRappels().get(0));
    }
    @Test 
    void planifierVisite_rappelFalse_neDoitPasEcrireDansLeJournal(){
        //Act + assert
        service.planifierVisite(2, LocalDate.of(2027, 3,1), "VACCIN", false, "Dr Léa", "Vaccin pour Rock", false);
        assertEquals(0, service.getJournalRappels().size());
    }
}
