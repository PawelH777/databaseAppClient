package pl.Vorpack.app.Dto;


import java.util.function.Function;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

    public class ActionButtonTableCell<S> extends TableCell<S, JFXButton> {

        private final JFXButton actionButton;

        public ActionButtonTableCell(String label, Function< S, S> function) {
            this.getStyleClass().add("action-button-table-cell");

            this.actionButton = new JFXButton(label);
            this.actionButton.setOnAction((ActionEvent e) -> {
                function.apply(getCurrentItem());
            });
            this.actionButton.setMaxWidth(Double.MAX_VALUE);
        }

        public S getCurrentItem() {
            return (S) getTableView().getItems().get(getIndex());
        }

        public static <S> Callback<TableColumn<S, JFXButton>, TableCell<S, JFXButton>> forTableColumn(String label, Function< S, S> function) {
            return param -> new ActionButtonTableCell<>(label, function);
        }

        @Override
        public void updateItem(JFXButton item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(actionButton);
            }
        }
    }

