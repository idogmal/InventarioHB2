package model;

public class Computer {
    private String tag;           // Etiqueta TI
    private String model;         // Modelo
    private String brand;         // Marca
    private String state;         // Estado do Computador (novo, usado, etc.)
    private String userName;      // Nome do usuário
    private String serialNumber;  // Número de Série
    private String windowsVersion;// Versão do Windows
    private String officeVersion; // Versão do Office
    private String location;      // Localização
    private String purchaseDate;  // Data de Compra

    // Construtor padrão
    public Computer() {}

    // Construtor completo
    public Computer(String tag, String model, String brand, String state, String userName, String serialNumber,
                    String windowsVersion, String officeVersion, String location, String purchaseDate) {
        setTag(tag);
        setModel(model);
        setBrand(brand);
        setState(state);
        setUserName(userName);
        setSerialNumber(serialNumber);
        setWindowsVersion(windowsVersion);
        setOfficeVersion(officeVersion);
        setLocation(location);
        setPurchaseDate(purchaseDate);
    }

    // Getters e Setters com validações básicas
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("A etiqueta TI não pode ser nula ou vazia.");
        }
        this.tag = tag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model != null ? model : "N/A";
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand != null ? brand : "N/A";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state != null ? state : "N/A";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode ser nulo ou vazio.");
        }
        this.userName = userName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber != null ? serialNumber : "N/A";
    }

    public String getWindowsVersion() {
        return windowsVersion;
    }

    public void setWindowsVersion(String windowsVersion) {
        this.windowsVersion = windowsVersion != null ? windowsVersion : "N/A";
    }

    public String getOfficeVersion() {
        return officeVersion;
    }

    public void setOfficeVersion(String officeVersion) {
        this.officeVersion = officeVersion != null ? officeVersion : "N/A";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null ? location : "N/A";
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate != null ? purchaseDate : "N/A";
    }

    // Método toString para representação textual
    @Override
    public String toString() {
        return String.format(
                "Tag: %s, Modelo: %s, Marca: %s, Estado: %s, Usuário: %s, Serial: %s, Windows: %s, Office: %s, Localização: %s, Compra: %s",
                tag, model, brand, state, userName, serialNumber, windowsVersion, officeVersion, location, purchaseDate
        );
    }
}
