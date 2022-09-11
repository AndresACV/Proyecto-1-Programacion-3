package sucursales.presentation.empleados;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class ViewEmpleados implements Observer {
    private JPanel panel;
    private JTextField nombreFld;
    private JButton buscarFld;
    private JButton agregarFld;
    private JTable empleadosFld;
    private JLabel nombreLbl;

    public ViewEmpleados() {
        buscarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllerEmpleados.buscar(nombreFld.getText());
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    ControllerEmpleados controllerEmpleados;
    ModelEmpleados modelEmpleados;

    public void setController(ControllerEmpleados controllerEmpleados) {
        this.controllerEmpleados = controllerEmpleados;
    }

    public void setModel(ModelEmpleados modelEmpleados) {
        this.modelEmpleados = modelEmpleados;
        modelEmpleados.addObserver(this);
    }

    @Override
    public void update(Observable updatedModel, Object parametros) {
        int[] cols = {TableModelEmpleados.CEDULA, TableModelEmpleados.NOMBRE};
        empleadosFld.setModel(new TableModelEmpleados(cols, modelEmpleados.getEmpleados()));
        empleadosFld.setRowHeight(30);
        this.panel.revalidate();
    }

}
