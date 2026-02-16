package UI;

public class PhoneDocument extends javax.swing.text.PlainDocument {
    @Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
            throws javax.swing.text.BadLocationException {
        if (str == null) return;
        
        // Allow digits and '+' sign
        StringBuilder filtered = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c) || c == '+') {
                filtered.append(c);
            }
        }
        
        String currentText = getText(0, getLength());
        String newText = currentText.substring(0, offs) + filtered.toString() + currentText.substring(offs);
        String validationText = newText.replaceAll("[^\\d+]", "");
        
        // Always allow input, just validate at the end
        if (validationText.isEmpty()) {
            super.insertString(offs, filtered.toString(), a);
        } else if (validationText.startsWith("09") && validationText.length() <= 11) {
            super.insertString(offs, filtered.toString(), a);
        } else if (validationText.startsWith("+63") && validationText.length() <= 13) {
            super.insertString(offs, filtered.toString(), a);
        } else if (validationText.length() <= 1) {
            // Allow initial digit input
            super.insertString(offs, filtered.toString(), a);
        }
    }
}
