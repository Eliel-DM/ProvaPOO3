package elieldm.provapoo.controller;

import elieldm.provapoo.dao.CursoDAO;
import elieldm.provapoo.dao.DisciplinaDAO;
import elieldm.provapoo.dao.ProfessorDAO;
import elieldm.provapoo.model.Curso;
import elieldm.provapoo.model.Disciplina;
import elieldm.provapoo.model.Professor;
import elieldm.provapoo.utils.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DisciplinaController {

    @FXML private TextField txtId;
    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Curso> cmbCurso;
    @FXML private ComboBox<Professor> cmbProfessor;
    @FXML private Label lblProfessoresSelecionados;
    @FXML private TableView<Disciplina> tableViewDisciplinas;
    @FXML private TableColumn<Disciplina, Long> colId;
    @FXML private TableColumn<Disciplina, String> colNome;
    @FXML private TableColumn<Disciplina, String> colDescricao;
    @FXML private TableColumn<Disciplina, String> colCurso;
    @FXML private TableColumn<Disciplina, String> colProfessores;

    private DisciplinaDAO disciplinaDAO;
    private CursoDAO cursoDAO;
    private ProfessorDAO professorDAO;

    private ObservableList<Disciplina> listaDisciplinas;
    private ObservableList<Curso> listaCursos;
    private ObservableList<Professor> listaProfessores;
    private Set<Professor> professoresSelecionados;

    @FXML
    public void initialize() {
        disciplinaDAO = new DisciplinaDAO();
        cursoDAO = new CursoDAO();
        professorDAO = new ProfessorDAO();

        listaDisciplinas = FXCollections.observableArrayList();
        listaCursos = FXCollections.observableArrayList();
        listaProfessores = FXCollections.observableArrayList();
        professoresSelecionados = new HashSet<>();

        cmbCurso.setItems(listaCursos);
        cmbCurso.setConverter(new javafx.util.StringConverter<Curso>() {
            @Override
            public String toString(Curso curso) {
                return curso != null ? curso.getNome() : "";
            }
            @Override
            public Curso fromString(String string) {
                return null;
            }
        });

        cmbProfessor.setItems(listaProfessores);
        cmbProfessor.setConverter(new javafx.util.StringConverter<Professor>() {
            @Override
            public String toString(Professor professor) {
                return professor != null ? professor.getNome() : "";
            }
            @Override
            public Professor fromString(String string) {
                return null;
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCurso.setCellValueFactory(cellData -> {
            Curso curso = cellData.getValue().getCurso();
            return new SimpleStringProperty(curso != null ? curso.getNome() : "");
        });
        colProfessores.setCellValueFactory(cellData -> {
            String nomes = cellData.getValue().getProfessores().stream()
                    .map(Professor::getNome)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(nomes);
        });

        tableViewDisciplinas.setItems(listaDisciplinas);

        tableViewDisciplinas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDisciplinaDetails(newValue));

        loadData();
    }

    private void showDisciplinaDetails(Disciplina disciplina) {
        if (disciplina != null) {
            txtId.setText(String.valueOf(disciplina.getId()));
            txtNome.setText(disciplina.getNome());
            txtDescricao.setText(disciplina.getDescricao());
            cmbCurso.getSelectionModel().select(disciplina.getCurso());

            professoresSelecionados.clear();
            professoresSelecionados.addAll(disciplina.getProfessores());
            updateProfessoresSelecionadosLabel();
        } else {
            handleClear();
        }
    }

    @FXML
    private void handleAddProfessor() {
        Professor selectedProfessor = cmbProfessor.getSelectionModel().getSelectedItem();
        if (selectedProfessor != null) {
            if (!professoresSelecionados.contains(selectedProfessor)) {
                professoresSelecionados.add(selectedProfessor);
                updateProfessoresSelecionadosLabel();
            } else {
                AlertUtil.showWarningAlert("Duplicidade", "Professor já adicionado a esta disciplina.");
            }
        } else {
            AlertUtil.showWarningAlert("Seleção Necessária", "Selecione um professor para adicionar.");
        }
    }

    @FXML
    private void handleRemoveProfessor() {
        Professor selectedProfessor = cmbProfessor.getSelectionModel().getSelectedItem();
        if (selectedProfessor != null) {
            if (professoresSelecionados.remove(selectedProfessor)) {
                updateProfessoresSelecionadosLabel();
            } else {
                AlertUtil.showWarningAlert("Não Encontrado", "Professor não está na lista de selecionados.");
            }
        } else {
            AlertUtil.showWarningAlert("Seleção Necessária", "Selecione um professor para remover.");
        }
    }

    private void updateProfessoresSelecionadosLabel() {
        if (professoresSelecionados.isEmpty()) {
            lblProfessoresSelecionados.setText("Nenhum professor selecionado");
        } else {
            String nomes = professoresSelecionados.stream()
                    .map(Professor::getNome)
                    .collect(Collectors.joining(", "));
            lblProfessoresSelecionados.setText("Professores Associados: " + nomes);
        }
    }

    @FXML
    private void handleNew() {
        handleClear();
    }

    @FXML
    private void handleSave() {
        try {
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();
            Curso curso = cmbCurso.getSelectionModel().getSelectedItem();

            if (nome.isEmpty() || curso == null) {
                AlertUtil.showErrorAlert("Erro de Validação", "Nome da Disciplina e Curso são obrigatórios.");
                return;
            }

            Disciplina novaDisciplina = new Disciplina(nome, descricao, curso);
            novaDisciplina.setProfessores(professoresSelecionados);
            disciplinaDAO.create(novaDisciplina);
            AlertUtil.showInformationAlert("Sucesso", "Disciplina salva com sucesso!");
            loadDisciplinas();
            handleClear();
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Erro", "Erro ao salvar disciplina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            Long id = Long.valueOf(txtId.getText());
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();
            Curso curso = cmbCurso.getSelectionModel().getSelectedItem();

            if (nome.isEmpty() || curso == null) {
                AlertUtil.showErrorAlert("Erro de Validação", "Nome da Disciplina e Curso são obrigatórios.");
                return;
            }

            Disciplina disciplinaToUpdate = disciplinaDAO.findById(id);
            if (disciplinaToUpdate != null) {
                disciplinaToUpdate.setNome(nome);
                disciplinaToUpdate.setDescricao(descricao);
                disciplinaToUpdate.setCurso(curso);
                disciplinaToUpdate.setProfessores(professoresSelecionados);
                disciplinaDAO.update(disciplinaToUpdate);
                AlertUtil.showInformationAlert("Sucesso", "Disciplina atualizada com sucesso!");
                loadDisciplinas();
                handleClear();
            } else {
                AlertUtil.showWarningAlert("Não Encontrado", "Disciplina com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Erro de Entrada", "ID deve ser um número válido.");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Erro", "Erro ao atualizar disciplina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Long id = Long.valueOf(txtId.getText());
            Disciplina disciplinaToDelete = disciplinaDAO.findById(id);
            if (disciplinaToDelete != null) {
                disciplinaDAO.delete(disciplinaToDelete);
                AlertUtil.showInformationAlert("Sucesso", "Disciplina excluída com sucesso!");
                loadDisciplinas();
                handleClear();
            } else {
                AlertUtil.showWarningAlert("Não Encontrado", "Disciplina com ID " + id + " não encontrada.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Erro de Entrada", "ID deve ser um número válido para exclusão.");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Erro", "Erro ao excluir disciplina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        txtId.clear();
        txtNome.clear();
        txtDescricao.clear();
        cmbCurso.getSelectionModel().clearSelection();
        cmbProfessor.getSelectionModel().clearSelection();
        professoresSelecionados.clear();
        updateProfessoresSelecionadosLabel();
        tableViewDisciplinas.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleListAll() {
        loadDisciplinas();
    }

    private void loadData() {
        loadCursos();
        loadProfessores();
        loadDisciplinas();
    }

    private void loadCursos() {
        listaCursos.clear();
        List<Curso> cursos = cursoDAO.findAll();
        listaCursos.addAll(cursos);
    }

    private void loadProfessores() {
        listaProfessores.clear();
        List<Professor> professores = professorDAO.findAll();
        listaProfessores.addAll(professores);
    }

    private void loadDisciplinas() {
        listaDisciplinas.clear();
        List<Disciplina> disciplinas = disciplinaDAO.findAllWithProfessores();
        listaDisciplinas.addAll(disciplinas);
    }
}
