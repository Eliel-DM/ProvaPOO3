package elieldm.provapoo.controller; // Importante: o pacote agora é 'controller'

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private void initialize() {
        System.out.println("HelloController inicializado. Tela inicial pronta!");
    }

    @FXML
    private void handleCadastroCursos(ActionEvent event) {
        carregarNovaTela("/elieldm/provapoo/view/cadastro-curso-view.fxml", "Cadastro de Cursos");
    }

    @FXML
    private void handleCadastroDisciplinas(ActionEvent event) {
        carregarNovaTela("/elieldm/provapoo/view/cadastro-disciplina-view.fxml", "Cadastro de Disciplinas");
    }

    @FXML
    private void handleCadastroProfessores(ActionEvent event) {
        carregarNovaTela("/elieldm/provapoo/view/cadastro-professor-view.fxml", "Cadastro de Professores");
    }

    @FXML
    private void handleCadastroTurmas(ActionEvent event) {
        carregarNovaTela("/elieldm/provapoo/view/cadastro-turma-view.fxml", "Cadastro de Turmas");
    }

    private void carregarNovaTela(String fxmlPath, String title) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);

            if (fxmlLocation == null) {
                System.err.println("Erro: Não foi possível encontrar o FXML em: " + fxmlPath);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(fxmlLoader.load());

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(scene);
            newStage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}