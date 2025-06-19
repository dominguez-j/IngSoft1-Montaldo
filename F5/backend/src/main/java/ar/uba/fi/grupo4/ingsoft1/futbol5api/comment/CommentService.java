package ar.uba.fi.grupo4.ingsoft1.futbol5api.comment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final FieldRepository fieldRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, AuthenticatedUserProvider authenticatedUserProvider, FieldRepository fieldRepository) {
        this.commentRepository = commentRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.fieldRepository = fieldRepository;
    }

    public void createComment(CommentCreateDTO dto) {
        Comment comment = dto.asComment(
                fieldRepository::findByName,
                authenticatedUserProvider::getAuthenticatedUser
        );
        commentRepository.save(comment);
    }

    public Page<CommentDTO> getCommentsByField(String fieldName, Pageable pageable) throws ItemNotFoundException {
        Field field = fieldRepository.findByName(fieldName)
                .orElseThrow(() -> new ItemNotFoundException("Field not found: " + fieldName));

        return commentRepository.findByField(field, pageable)
                .map(CommentDTO::new);
    }
}
