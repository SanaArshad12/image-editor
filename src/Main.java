import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Stack;

class ImageEditorPanel extends JPanel {
    private BufferedImage image;
    private JLabel imageLabel;
    private Stack<BufferedImage> imageHistory = new Stack<>();

    public ImageEditorPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLUE); // Set panel background color

        JPanel buttonPanel = new JPanel(new GridBagLayout());
//       buttonPanel.setBackground(Color.BLUE);
        buttonPanel.setBackground(new Color(51, 153, 255)); // RGB for skyblue
        // RGB for light blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton loadButton = createButton("Load Image");
        JButton saveButton = createButton("Save Image");
        JButton cropButton = createButton("Crop Image");
        JButton resizeButton = createButton("Resize Image");
        JButton rotateButton = createButton("Rotate Image");
        JButton filterButton = createButton("Apply Filter");
        JButton brightnessButton = createButton("Adjust Brightness");
        JButton contrastButton = createButton("Adjust Contrast");
        JButton invertButton = createButton("Invert Colors");
        JButton blurButton = createButton("Apply Blur");
        JButton undoButton = createButton("Undo");

        loadButton.addActionListener(new LoadButtonListener());
        saveButton.addActionListener(new SaveButtonListener());
        cropButton.addActionListener(new CropButtonListener());
        resizeButton.addActionListener(new ResizeButtonListener());
        rotateButton.addActionListener(new RotateButtonListener());
        filterButton.addActionListener(new FilterButtonListener());
        brightnessButton.addActionListener(new BrightnessButtonListener());
        contrastButton.addActionListener(new ContrastButtonListener());
        invertButton.addActionListener(new InvertButtonListener());
        blurButton.addActionListener(new BlurButtonListener());
        undoButton.addActionListener(new UndoButtonListener());

        JPanel topRow = new JPanel(new FlowLayout());
//        topRow.setBackground(new Color(51, 153, 255));
        topRow.setBackground(new Color(51, 153, 255)); // RGB for skyblue

        topRow.add(loadButton);
        topRow.add(saveButton);

        JPanel editRow = new JPanel(new GridLayout(2, 5, 5, 5));
           editRow.setBackground(new Color(51, 153, 255)); // RGB for skyblue


        editRow.add(cropButton);
        editRow.add(resizeButton);
        editRow.add(rotateButton);
        editRow.add(filterButton);
        editRow.add(brightnessButton);
        editRow.add(contrastButton);
        editRow.add(invertButton);
        editRow.add(blurButton);
        editRow.add(undoButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(topRow, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(editRow, gbc);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
//        button.setBackground(new Color(51, 153, 255));
        button.setBackground(new Color(173, 216, 230)); // RGB for light blue

        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    private void displayImage() {
        if (image != null) {
            imageLabel.setIcon(new ImageIcon(image));
            revalidate();
            repaint();
        } else {
            imageLabel.setIcon(null);
        }
    }

    private void saveToHistory() {
        if (image != null) {
            BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            Graphics g = copy.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            imageHistory.push(copy);
        }
    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(ImageEditorPanel.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    image = ImageIO.read(file);
                    imageHistory.clear();
                    saveToHistory();
                    displayImage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ImageEditorPanel.this, "Error loading image: " + ex.getMessage());
                }
            }
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(ImageEditorPanel.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        ImageIO.write(image, "png", file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ImageEditorPanel.this, "Error saving image: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private class CropButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                JTextField xField = new JTextField(5);
                JTextField yField = new JTextField(5);
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);

                JPanel cropPanel = new JPanel();
                cropPanel.add(new JLabel("X:"));
                cropPanel.add(xField);
                cropPanel.add(new JLabel("Y:"));
                cropPanel.add(yField);
                cropPanel.add(new JLabel("Width:"));
                cropPanel.add(widthField);
                cropPanel.add(new JLabel("Height:"));
                cropPanel.add(heightField);

                int result = JOptionPane.showConfirmDialog(null, cropPanel, "Enter Crop Dimensions", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int x = Integer.parseInt(xField.getText());
                        int y = Integer.parseInt(yField.getText());
                        int width = Integer.parseInt(widthField.getText());
                        int height = Integer.parseInt(heightField.getText());

                        if (x + width <= image.getWidth() && y + height <= image.getHeight()) {
                            saveToHistory();
                            image = image.getSubimage(x, y, width, height);
                            displayImage();
                        } else {
                            JOptionPane.showMessageDialog(ImageEditorPanel.this, "Crop dimensions are out of bounds");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ImageEditorPanel.this, "Invalid dimensions");
                    }
                }
            }
        }
    }

    private class ResizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);

                JPanel resizePanel = new JPanel();
                resizePanel.add(new JLabel("New Width:"));
                resizePanel.add(widthField);
                resizePanel.add(new JLabel("New Height:"));
                resizePanel.add(heightField);

                int result = JOptionPane.showConfirmDialog(null, resizePanel, "Enter New Dimensions", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int newWidth = Integer.parseInt(widthField.getText());
                        int newHeight = Integer.parseInt(heightField.getText());

                        saveToHistory();
                        Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g2d = resized.createGraphics();
                        g2d.drawImage(tmp, 0, 0, null);
                        g2d.dispose();

                        image = resized;
                        displayImage();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ImageEditorPanel.this, "Invalid dimensions");
                    }
                }
            }
        }
    }

    private class RotateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                String degrees = JOptionPane.showInputDialog("Enter degrees to rotate:");
                try {
                    int angle = Integer.parseInt(degrees);
                    saveToHistory();
                    image = rotateImage(image, angle);
                    displayImage();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ImageEditorPanel.this, "Invalid input for degrees");
                }
            }
        }

        private BufferedImage rotateImage(BufferedImage img, int angle) {
            int w = img.getWidth();
            int h = img.getHeight();
            BufferedImage rotated = new BufferedImage(w, h, img.getType());
            Graphics2D g2d = rotated.createGraphics();
            g2d.rotate(Math.toRadians(angle), w / 2, h / 2);
            g2d.drawImage(img, null, 0, 0);
            g2d.dispose();
            return rotated;
        }
    }

    private class FilterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                String[] options = {"Grayscale", "Sepia"};
                int choice = JOptionPane.showOptionDialog(null, "Choose a filter", "Filter Options",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice != -1) {
                    saveToHistory();
                    image = applyFilter(image, options[choice]);
                    displayImage();
                }
            }
        }

        private BufferedImage applyFilter(BufferedImage img, String filter) {
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage filteredImage = new BufferedImage(width, height, img.getType());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(img.getRGB(x, y));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    int newPixel;

                    switch (filter) {
                        case "Grayscale":
                            int gray = (r + g + b) / 3;
                            newPixel = new Color(gray, gray, gray).getRGB();
                            break;
                        case "Sepia":
                            int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                            int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                            int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                            newPixel = new Color(Math.min(tr, 255), Math.min(tg, 255), Math.min(tb, 255)).getRGB();
                            break;
                        default:
                            newPixel = color.getRGB();
                    }
                    filteredImage.setRGB(x, y, newPixel);
                }
            }
            return filteredImage;
        }
    }

    private class BrightnessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                String input = JOptionPane.showInputDialog("Enter brightness adjustment value (-255 to 255):");
                try {
                    int brightness = Integer.parseInt(input);
                    saveToHistory();
                    image = adjustBrightness(image, brightness);
                    displayImage();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ImageEditorPanel.this, "Invalid input for brightness");
                }
            }
        }

        private BufferedImage adjustBrightness(BufferedImage img, int value) {
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage adjustedImage = new BufferedImage(width, height, img.getType());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(img.getRGB(x, y));
                    int r = clamp(color.getRed() + value);
                    int g = clamp(color.getGreen() + value);
                    int b = clamp(color.getBlue() + value);
                    adjustedImage.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
            return adjustedImage;
        }

        private int clamp(int value) {
            return Math.max(0, Math.min(value, 255));
        }
    }

    private class ContrastButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                String input = JOptionPane.showInputDialog("Enter contrast adjustment value (-100 to 100):");
                try {
                    int contrast = Integer.parseInt(input);
                    saveToHistory();
                    image = adjustContrast(image, contrast);
                    displayImage();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ImageEditorPanel.this, "Invalid input for contrast");
                }
            }
        }

        private BufferedImage adjustContrast(BufferedImage img, int value) {
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage adjustedImage = new BufferedImage(width, height, img.getType());
            float scaleFactor = (259 * (value + 255)) / (255 * (259 - value));

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(img.getRGB(x, y));
                    int r = clamp((int) (scaleFactor * (color.getRed() - 128) + 128));
                    int g = clamp((int) (scaleFactor * (color.getGreen() - 128) + 128));
                    int b = clamp((int) (scaleFactor * (color.getBlue() - 128) + 128));
                    adjustedImage.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
            return adjustedImage;
        }

        private int clamp(int value) {
            return Math.max(0, Math.min(value, 255));
        }
    }

    private class InvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                saveToHistory();
                image = invertColors(image);
                displayImage();
            }
        }

        private BufferedImage invertColors(BufferedImage img) {
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage invertedImage = new BufferedImage(width, height, img.getType());

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(img.getRGB(x, y));
                    int r = 255 - color.getRed();
                    int g = 255 - color.getGreen();
                    int b = 255 - color.getBlue();
                    invertedImage.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
            return invertedImage;
        }
    }

    private class BlurButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (image != null) {
                saveToHistory();
                image = applyBlur(image);
                displayImage();
            }
        }

        private BufferedImage applyBlur(BufferedImage img) {
            int radius = 5;
            int size = radius * 2 + 1;
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage blurredImage = new BufferedImage(width, height, img.getType());
            int[] pixels = new int[width * height];
            img.getRGB(0, 0, width, height, pixels, 0, width);
            int[] blurredPixels = new int[width * height];

            for (int y = radius; y < height - radius; y++) {
                for (int x = radius; x < width - radius; x++) {
                    int r = 0, g = 0, b = 0;
                    for (int ky = -radius; ky <= radius; ky++) {
                        for (int kx = -radius; kx <= radius; kx++) {
                            int pixel = pixels[(y + ky) * width + (x + kx)];
                            Color color = new Color(pixel);
                            r += color.getRed();
                            g += color.getGreen();
                            b += color.getBlue();
                        }
                    }
                    int numPixels = size * size;
                    Color newColor = new Color(r / numPixels, g / numPixels, b / numPixels);
                    blurredPixels[y * width + x] = newColor.getRGB();
                }
            }
            blurredImage.setRGB(0, 0, width, height, blurredPixels, 0, width);
            return blurredImage;
        }
    }

    private class UndoButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!imageHistory.isEmpty()) {
                image = imageHistory.pop();
                displayImage();
            }
        }
    }
}
 class ImageEditor {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Image Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new ImageEditorPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
