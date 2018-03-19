package pl.Vorpack.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.domain.PersistenceInfo;
import pl.Vorpack.app.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Paweł on 2018-02-01.
 */
public class Main extends Application {

    public static final String LOGIN_PANE_FXML = "/fxml/main/LogInPane.fxml";


    public static void main(String[] args) {

        Dimiensions dim;
        Map<String, String> properties = new HashMap<String, String>();

        properties = PersistenceInfo.putTokens();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", properties);
        EntityManager entityManager = emf.createEntityManager();

        entityManager.getTransaction().begin();
        TypedQuery<User> users = entityManager.createQuery("SELECT u FROM User u WHERE u.login = 'Admin'", User.class);

        if(users.getResultList().size() == 0){
            String pass = DigestUtils.sha1Hex("password");
            User user = new User("Admin", pass, true);
            entityManager.persist(user);
        }

        TypedQuery<Dimiensions> dims = entityManager.createQuery("SELECT d FROM Dimiensions d", Dimiensions.class);


        if(dims.getResultList().size() == 0){
            dim = new Dimiensions(30.0,30.0,1.0, 0.035);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0,1.5, 0.050);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0, 2.0, 0.085);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0,2.5, 0.100);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0,3.0, 0.120);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0,3.5, 0.145);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0,4.0, 0.170);
            entityManager.persist(dim);

            dim = new Dimiensions(30.0,30.0,5.0, 0.220);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,1.0, 0.044);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,1.5, 0.072);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,2.0, 0.100);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,2.5, 0.116);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,3.0, 0.140);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,3.2, 0.152);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,3.5, 0.163);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,4.0, 0.175);
            entityManager.persist(dim);

            dim = new Dimiensions(35.0,35.0,5.0, 0.235);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,2.0, 0.115);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,2.5, 0.140);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,3.0, 0.150);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,3.5, 0.182);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,4.0, 0.200);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,5.0, 0.270);
            entityManager.persist(dim);

            dim = new Dimiensions(40.0,40.0,8.0, 0.400);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,1.5, 0.105);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,1.5, 0.105);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,1.5, 0.105);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,1.5, 0.105);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,2.0, 0.130);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,2.5, 0.155);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,3.0, 0.180);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,3.5, 0.208);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,4.0, 0.235);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,4.5, 0.268);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,5.0, 0.300);
            entityManager.persist(dim);

            dim = new Dimiensions(45.0,45.0,6.0, 0.335);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,1.5, 0.107);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,1.8, 0.126);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,2.0, 0.130);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,2.5, 0.176);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,3.0, 0.200);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,3.5, 0.247);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,4.0, 0.260);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,5.0, 0.325);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,6.0, 0.390);
            entityManager.persist(dim);

            dim = new Dimiensions(50.0,50.0,8.0, 0.520);
            entityManager.persist(dim);

            dim = new Dimiensions(55.0,55.0,2.5, 0.185);
            entityManager.persist(dim);

            dim = new Dimiensions(55.0,55.0,3.0, 0.220);
            entityManager.persist(dim);

            dim = new Dimiensions(55.0,55.0,4.0, 0.290);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,2.0, 0.175);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,2.5, 0.225);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,3.0, 0.240);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0, 3.5, 0.255);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,4.0, 0.325);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,4.5, 0.360);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,5.0, 0.395);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,6.0, 0.470);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,7.0, 0.545);
            entityManager.persist(dim);

            dim = new Dimiensions(60.0,60.0,8.0, 0.625);
            entityManager.persist(dim);

            dim = new Dimiensions(70.0,70.0,2.0, 0.195);
            entityManager.persist(dim);

            dim = new Dimiensions(70.0,70.0,3.0, 0.285);
            entityManager.persist(dim);

            dim = new Dimiensions(70.0,70.0,3.5, 0.335);
            entityManager.persist(dim);

            dim = new Dimiensions(70.0,70.0,4.0, 0.385);
            entityManager.persist(dim);

            dim = new Dimiensions(70.0,70.0,5.0, 0.490);
            entityManager.persist(dim);

            dim = new Dimiensions(70.0,70.0,6.0, 0.595);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,2.0, 0.200);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,3.0, 0.335);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,3.5, 0.375);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,4.0, 0.415);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,4.5, 0.468);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,5.0, 0.520);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,5.5, 0.580);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,6.0, 0.610);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,7.0, 0.700);
            entityManager.persist(dim);

            dim = new Dimiensions(75.0,75.0,8.0, 0.810);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,2.0, 0.215);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,3.0, 0.340);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,3.5, 0.385);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,4.0, 0.430);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,5.0, 0.540);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,6.0, 0.650);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,7.0, 0.760);
            entityManager.persist(dim);

            dim = new Dimiensions(80.0,80.0,8.0, 0.840);
            entityManager.persist(dim);

            dim = new Dimiensions(90.0,90.0,2.0, 0.260);
            entityManager.persist(dim);

            dim = new Dimiensions(90.0,90.0,3.0, 0.360);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,2.0, 0.310);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,3.0, 0.310);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,3.5, 0.465);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,4.0, 0.530);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,5.0, 0.670);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,6.0, 0.810);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,7.0, 0.940);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,8.0, 1.100);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,9.0, 1.230);
            entityManager.persist(dim);

            dim = new Dimiensions(100.0,100.0,10.0, 1.350);
            entityManager.persist(dim);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(LOGIN_PANE_FXML));

        BorderPane borderPane = loader.load();
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("VorApp");
        primaryStage.show();
    }
}




