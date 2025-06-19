package ar.uba.fi.grupo4.ingsoft1.futbol5api.comment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@Tag(name = "Comments")
public class CommentRestController {
    private final CommentService commentService;

    @Autowired
    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "Creates a new comment")
    public ResponseEntity<Void> createComment(@RequestBody CommentCreateDTO comment) {
        commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{fieldName}", produces = "application/json")
    @Operation(summary = "Gets paginated comments for a specific field")
    @ResponseStatus(HttpStatus.OK)
    public Page<CommentDTO> getCommentsByField(
            @PathVariable String fieldName,
            @PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC)
            @Valid @ParameterObject Pageable pageable
    ) throws ItemNotFoundException {
        return commentService.getCommentsByField(fieldName, pageable);
    }
}
