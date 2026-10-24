package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.message.MessageRequest;
import ma.fsbm.lias.dto.message.MessageResponse;
import ma.fsbm.lias.entity.Equipe;
import ma.fsbm.lias.entity.Message;
import ma.fsbm.lias.entity.Utilisateur;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.EquipeRepository;
import ma.fsbm.lias.repository.MessageRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EquipeRepository equipeRepository;

    public MessageService(MessageRepository messageRepository,
                          UtilisateurRepository utilisateurRepository,
                          EquipeRepository equipeRepository) {
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.equipeRepository = equipeRepository;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> findReceived(Long userId) {
        return messageRepository.findReceivedByUser(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> findSent(Long userId) {
        return messageRepository.findByExpediteurIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse).toList();
    }

    public MessageResponse send(Long expediteurId, MessageRequest request) {
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", expediteurId));

        Message message = new Message();
        message.setExpediteur(expediteur);
        message.setSujet(request.sujet());
        message.setContenu(request.contenu());

        if (request.destinataireId() != null) {
            Utilisateur destinataire = utilisateurRepository.findById(request.destinataireId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", request.destinataireId()));
            message.setDestinataire(destinataire);
        }
        if (request.equipeId() != null) {
            Equipe equipe = equipeRepository.findById(request.equipeId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Equipe", request.equipeId()));
            message.setEquipe(equipe);
        }
        return toResponse(messageRepository.save(message));
    }

    public MessageResponse markAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Message", id));
        message.setLu(true);
        return toResponse(messageRepository.save(message));
    }

    public long countUnread(Long userId) {
        return messageRepository.countByDestinataireIdAndLuFalse(userId);
    }

    private MessageResponse toResponse(Message m) {
        Utilisateur dest = m.getDestinataire();
        Equipe eq = m.getEquipe();
        return new MessageResponse(
                m.getId(),
                m.getExpediteur().getId(),
                m.getExpediteur().getEmail(),
                m.getExpediteur().getEmail(),
                dest != null ? dest.getId() : null,
                dest != null ? dest.getEmail() : null,
                eq != null ? eq.getId() : null,
                eq != null ? eq.getNom() : null,
                m.getSujet(),
                m.getContenu(),
                m.isLu(),
                m.getCreatedAt());
    }
}
