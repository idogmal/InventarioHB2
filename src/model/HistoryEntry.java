package model;

import java.time.LocalDateTime;

public class HistoryEntry {

    public enum ActionType {
        ADICIONAR, EDITAR, EXCLUIR
    }

    private ActionType action;       // Ação realizada (Adicionar, Editar, Excluir)
    private String user;             // Nome do usuário que realizou a ação
    private LocalDateTime timestamp; // Data e hora da alteração
    private String description;      // Detalhes sobre a alteração

    // Construtor completo
    public HistoryEntry(ActionType action, String user, LocalDateTime timestamp, String description) {
        setAction(action);
        setUser(user);
        setTimestamp(timestamp);
        setDescription(description);
    }

    // Construtor sem timestamp (atribui o horário atual automaticamente)
    public HistoryEntry(ActionType action, String user, String description) {
        this(action, user, LocalDateTime.now(), description);
    }

    // Getters e Setters com validações
    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        if (action == null) {
            throw new IllegalArgumentException("A ação não pode ser nula.");
        }
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode ser nulo ou vazio.");
        }
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("O timestamp não pode ser nulo.");
        }
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "Sem descrição.";
    }

    // Método toString para representação textual
    @Override
    public String toString() {
        return String.format("[%s] %s realizou a ação '%s': %s",
                timestamp, user, action, description);
    }
}
