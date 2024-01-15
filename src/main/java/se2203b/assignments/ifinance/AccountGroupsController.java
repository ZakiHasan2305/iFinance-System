package se2203b.assignments.ifinance;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;


public class AccountGroupsController implements Initializable {

    @FXML
    private Button exitButton;

    @FXML
    private TextField grpNameField;

    @FXML
    private TreeView<String> stringTreeView;

    @FXML
    private TitledPane edittingGrpPane;

    private String option;
    private String selectedGroupName;

    private TreeItem<String> currTreeItm;

    // Create a context menu with CRUD options
    MenuItem addGrp = new MenuItem("Add New Group");
    MenuItem modGrp = new MenuItem("Change Group Name");
    MenuItem delGrp = new MenuItem("Delete Group");
    ContextMenu contextMenu = new ContextMenu(addGrp, modGrp, delGrp);

    ArrayList<String> rootsList = new ArrayList<>(Arrays.asList("Assets", "Liabilities", "Income", "Expenses"));

    private AccountGroupsAdapter accGrpAdapter;
    private AccountCategoryAdapter accCatAdapter;

    public void setUserModel(AccountGroupsAdapter accGroups,AccountCategoryAdapter accCat){
        accGrpAdapter = accGroups;
        accCatAdapter = accCat;
    }

    public void setIFinanceController(IFinanceController controller){
        IFinanceController iFinanceController = controller;
    }

    @FXML
    void exit(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(ActionEvent event) throws SQLException {
        if(option == "add") {
            int parent;
            String element;
            if (AccountGroupsAdapter.getID(selectedGroupName)!=0){
                parent = AccountGroupsAdapter.getID(selectedGroupName);
                element = AccountGroupsAdapter.getElement(selectedGroupName);
            } else{
                parent = 0;
                element = selectedGroupName;
            }
            AccountGroupsAdapter.createGroup(grpNameField.getText(), parent, element);
            locateTreeItem(stringTreeView.getRoot(), selectedGroupName).getChildren().add(new TreeItem<>(grpNameField.getText()));
        }
        else if(option == "modify"){
            AccountGroupsAdapter.updateGroup(selectedGroupName, grpNameField.getText());
            locateTreeItem(stringTreeView.getRoot(), selectedGroupName).setValue(grpNameField.getText());
            stringTreeView.setShowRoot(true);
            stringTreeView.setShowRoot(false);
        }
        stringTreeView.getSelectionModel().select(locateTreeItem(stringTreeView.getRoot(), grpNameField.getText()));
        grpNameField.clear();
        grpNameField.setDisable(true);
        edittingGrpPane.setDisable(true);
    }


    @FXML
    public void selecItem(ContextMenuEvent contextMenuEvent) {
        TreeItem<String> selItem = stringTreeView.getSelectionModel().getSelectedItem();
        final boolean[] canEdit = {true};
        final boolean[] canDelete = {true};
        final boolean[] canAdd = {true};

        // Register an event handler for mouse clicks on the tree view
        stringTreeView.setOnMouseClicked((MouseEvent event) -> {
            if (!stringTreeView.getSelectionModel().getSelectedItem().equals(selItem)) {
                contextMenu.hide();
            }
                // Check if the right mouse button was clicked
                if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    if (rootsList.contains(selItem.getValue())) {
                        canEdit[0] = false;
                        canDelete[0] = false;
                        canAdd[0] = true;
                    }
                    else if (!selItem.getChildren().isEmpty()) {
                        canEdit[0] = true;
                        canDelete[0] = false;
                        canAdd[0] = true;
                    }
                    modGrp.setDisable(!canEdit[0]);
                    delGrp.setDisable(!canDelete[0]);
                    addGrp.setDisable(!canAdd[0]);
                    // Show the context menu at the mouse position
                    contextMenu.show(stringTreeView, event.getScreenX(), event.getScreenY());
                }
        });

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String id = ((MenuItem) event.getSource()).getText();
                switch (id) {
                    case "Add New Group":
                        option = "add";
                        selectedGroupName = selItem.getValue();
                        currTreeItm = selItem;
                        edittingGrpPane.setDisable(false);
                        grpNameField.setDisable(false);
                        grpNameField.requestFocus();
                        break;
                    case "Change Group Name":
                        option = "modify";
                        selectedGroupName = selItem.getValue();
                        currTreeItm = selItem;
                        edittingGrpPane.setDisable(false);
                        grpNameField.setDisable(false);
                        grpNameField.requestFocus();
                        break;
                    case "Delete Group":
                        selectedGroupName = selItem.getValue();
                        try {
                            AccountGroupsAdapter.deleteGroup(AccountGroupsAdapter.getID(selectedGroupName));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        remAndHideTreeItem(selItem, stringTreeView);
                        break;
                    default:
                        break;
                }
            }
        };
        addGrp.setOnAction(eventHandler);
        modGrp.setOnAction(eventHandler);
        delGrp.setOnAction(eventHandler);
    }

    public TreeItem<String> locateTreeItem(TreeItem<String> root, String value) {
        if (root.getValue().equals(value)) {
            return root;
        }

        for (TreeItem<String> child : root.getChildren()) {
            TreeItem<String> result = locateTreeItem(child, value);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public void remAndHideTreeItem(TreeItem<String> item, TreeView<String> treeView) {
        TreeItem<String> parent = item.getParent();
        if (parent != null) {
            parent.getChildren().remove(item);
            treeView.setShowRoot(true);
            treeView.setShowRoot(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        grpNameField.setDisable(true);
        edittingGrpPane.setDisable(true);
        fillTree();
    }

    public void fillTree() {
        TreeItem<String> assets = new TreeItem<>("Assets");
        TreeItem<String> liabilities = new TreeItem<>("Liabilities");
        TreeItem<String> income = new TreeItem<>("Income");
        TreeItem<String> expenses = new TreeItem<>("Expenses");

        stringTreeView.setRoot(new TreeItem<>("dummy"));
        stringTreeView.setShowRoot(false);
        stringTreeView.getRoot().getChildren().addAll(assets, liabilities, income, expenses);


        ArrayList<Group> grpList = null;
        try {
            grpList = AccountGroupsAdapter.GroupsList();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (Group group : grpList) {
            TreeItem<String> parentRt = null;
            if (group.getElement().getName() != null) {
                if (group.getElement().getName().equals("Assets")) {
                    parentRt = assets;
                }
                if (group.getElement().getName().equals("Liabilities")) {
                    parentRt = liabilities;
                }
                if (group.getElement().getName().equals("Income")) {
                    parentRt = income;
                }
                if (group.getElement().getName().equals("Expenses")) {
                    parentRt = expenses;
                }
                if (group.getParent()!=null) {
                    TreeItem<String> innerParentRt = locateTreeItem(stringTreeView.getRoot(),group.getParent().getName());
                    parentRt = innerParentRt;
                }
                parentRt.getChildren().add(new TreeItem<>(group.getName()));

            }
        }
    }

}