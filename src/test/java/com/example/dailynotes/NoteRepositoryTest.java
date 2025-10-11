package com.example.dailynotes;

import com.example.dailynotes.entity.Note;
import com.example.dailynotes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class NoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void whenFindById_thenReturnNote(){

        Note note = new Note("Тест", "Содержание тестовой заметки",10.0);
        entityManager.persist(note);
        entityManager.flush();

        Note found = noteRepository.findById(note.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo(note.getTitle());

    }
}
