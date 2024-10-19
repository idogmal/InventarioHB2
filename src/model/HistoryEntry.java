package model;

import java.time.LocalDateTime;

public class HistoryEntry {
    private String action;  // Ação realizada (Adicionar, Editar, Excluir)
    private String user;    // Nome do usuário que realizou a ação
    private LocalDateTime timestamp;  // Data e hora da alteração
    private String description;  // Detalhes sobre a alteração

    public HistoryEntry(String action, String user, LocalDateTime timestamp, String description) {
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
}
