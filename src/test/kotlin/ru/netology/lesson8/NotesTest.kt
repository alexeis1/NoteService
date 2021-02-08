package ru.netology.lesson8

import org.junit.Test

class NotesTest {

    @Test(expected = CommentAlreadyExistsException::class)
    fun delete_fails() {
        var notes = Notes(1)
        
        notes.delete(1)
    }
}