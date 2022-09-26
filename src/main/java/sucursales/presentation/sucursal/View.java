package sucursales.presentation.sucursal;

import sucursales.Application;
import sucursales.logic.Sucursal;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {

    sucursales.presentation.sucursal.Controller controller;
    sucursales.presentation.sucursal.Model model;

    private JPanel panel;
    private JFrame window;
    private Graphics graphics;
    private BufferedImage result;

    private JLabel codigoLabel;
    private JTextField codigoField;

    private JLabel referenciaLabel;
    private JTextField referenciaField;

    private JButton guardarButton;
    private JButton cancelarButton;

    private JLabel direccionLabel;
    private JTextField direccionField;

    private JLabel zonajeLabel;
    private JTextField zonajeField;

    private JLabel mapLabel;
    private Image mapImage;

    private JLabel selectedLabel;
    private Image sucursalSelectedImage;

    private JLabel unselectedLabel;
    private Image sucursalUnselectedImage;

    private JLabel chirulito;

    private int x = 0;
    private int y = 0;

    public View() throws IOException {
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clean();
                if (validate()) {
                    Sucursal n = null;
                    JLabel l = null;
                    try {
                        n = take();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        controller.guardar(n);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.hide();
                cleanMap();
            }
        }
        );
        mapLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cleanMap();

                x = e.getX();
                y = e.getY();
                chirulito.setLocation(x - 15, y - 31);
                mapLabel.add(chirulito);
            }
        });
    }

    public JPanel getPanel() { return panel; }

    public void setController(sucursales.presentation.sucursal.Controller controller) { this.controller = controller; }

    public void setModel(sucursales.presentation.sucursal.Model model) {
        this.model = model;
        model.addObserver(this);
    }

    private void cleanMap(){
        mapLabel.remove(chirulito);
        mapLabel.setIcon(new ImageIcon(result));
    }

    @Override
    public void update(Observable updatedModel, Object parametros) {
        Sucursal current = model.getCurrent();
            this.codigoField.setEnabled(model.getModo() == Application.MODO_AGREGAR);
            this.codigoField.setText(current.getCodigo());
            referenciaField.setText(current.getReferencia());
            direccionField.setText(current.getDireccion());
            if(String.valueOf(current.getPorcentajeZonaje()).equals("0.0")){
                zonajeField.setText("");
            }
            else{
                zonajeField.setText(String.valueOf(current.getPorcentajeZonaje()));
            }

            if(model.getModo() == 0) { cleanMap(); }
            if(model.getModo() == 1) {
                chirulito.setLocation(current.getX() - 15, current.getY() - 31);
                chirulito.setToolTipText("<html>" + current.getReferencia() + "<br/>" + current.getDireccion() +"</html>");
                mapLabel.add(chirulito);
                mapLabel.setIcon(new ImageIcon(result));
            }
            this.panel.validate();
    }

    public Sucursal take() {
        Sucursal s = new Sucursal();
        s.setCodigo(codigoField.getText());
        s.setReferencia(referenciaField.getText());
        s.setDireccion(direccionField.getText());
        s.setPorcentajeZonaje(Double.parseDouble(zonajeField.getText()));
        s.setX(x);
        s.setY(y);
        return s;
    }

    public void clean(){
        codigoLabel.setBorder(new EmptyBorder(0, 0, 2, 0));
        referenciaLabel.setBorder(new EmptyBorder(0, 0, 2, 0));
        direccionLabel.setBorder(new EmptyBorder(0, 0, 2, 0));
        zonajeLabel.setBorder(new EmptyBorder(0, 0, 2, 0));
    }
    private boolean validate() {

        boolean valid = true;
        String mensajeError = "";
        int concatenaciones = 0;

        if (codigoField.getText().isEmpty()) {
            valid = false;
            codigoLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Codigo requerido. "; concatenaciones++;
        } else if(!codigoField.getText().matches("[0-9]+")){
            valid = false;
            codigoLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Codigo debe ser numerico. ";        }
        else {
            codigoField.setBorder(null);
        }

        if (referenciaField.getText().length() == 0) {
            valid = false;
            referenciaLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Referencia requerida. "; concatenaciones++;
        } else if(!referenciaField.getText().matches("^[a-z\\sA-Z]+$")){
            valid = false;
            referenciaLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Referencia no puede ser numerica. ";
        } else {
            referenciaField.setBorder(null);
        }
        if (direccionField.getText().isEmpty()) {
            valid = false;
            direccionLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Direccion requerida. "; concatenaciones++;
        } else {
            codigoLabel.setBorder(null);
        }

        if (zonajeField.getText().isEmpty()) {
            valid = false;
            zonajeLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Zonaje requerido. "; concatenaciones++;
        } else if(!zonajeField.getText().matches("^[0-9]+\\.?[0-9]*$")) {
            valid = false;
            zonajeLabel.setBorder(Application.BORDER_ERROR);
            mensajeError += "Zonaje debe ser numerico. ";
        } else {
            codigoLabel.setBorder(null);
        }

        if (x == 0 && y == 0) {
            valid = false;
            mapLabel.setBorder(Application.BORDER_ERROR);
            JOptionPane.showMessageDialog(panel, "Debe seleccionar un lugar en el mapa","ERROR",JOptionPane.ERROR_MESSAGE);
        } else {
            mapLabel.setBorder(null);
        }

        if(concatenaciones == 4){
            JOptionPane.showMessageDialog(panel, "Todos los campos son requeridos","ERROR",JOptionPane.ERROR_MESSAGE);
        } else if(!mensajeError.equals("")){
            JOptionPane.showMessageDialog(panel, mensajeError,"ERROR",JOptionPane.ERROR_MESSAGE);
        }
        return valid;
    }

    private void createUIComponents() throws IOException {
        // TODO: place custom component creation code here
        mapLabel = new JLabel();
        mapImage = ImageIO.read(new File("src/main/resources/MapCR.png"));
        mapImage = mapImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        result = new BufferedImage(400,400, BufferedImage.TYPE_INT_ARGB);
        graphics = result.getGraphics();
        graphics.drawImage(mapImage, 10, 10, mapLabel);
        mapLabel.setIcon(new ImageIcon(result));

        selectedLabel = new JLabel();
        sucursalSelectedImage = ImageIO.read(new File("src/main/resources/Sucursal.png"));
        selectedLabel.setIcon(new ImageIcon(sucursalSelectedImage));

        unselectedLabel = new JLabel();
        sucursalUnselectedImage = ImageIO.read(new File("src/main/resources/SucursalSel.png"));
        unselectedLabel.setIcon(new ImageIcon(sucursalUnselectedImage));

        chirulito = new JLabel();
        chirulito.setIcon(new ImageIcon(sucursalUnselectedImage));
        chirulito.setSize(30, 30);
        chirulito.setVisible(true);
        chirulito.setToolTipText("Sucursal");

        mapLabel.add(chirulito);

//        Sucursal sucursal = new Sucursal("006", "Limon", "Limon, Cahuita, Playa Negra", 4.0);
//        JLabel labelPrueba = new JLabel();
//        labelPrueba.putClientProperty(sucursal.getReferencia(), sucursal);
//        Sucursal x = (Sucursal) labelPrueba.getClientProperty("Limon");
//        mapLabel.putClientProperty(sucursal.getReferencia(), sucursal);
//        mapLabel.getClientProperty(sucursal.getReferencia());
//        graphics.drawImage(mapImage, 30, 40,mapLabel);
//        mapLabel.setIcon(new ImageIcon(result));
    }
}