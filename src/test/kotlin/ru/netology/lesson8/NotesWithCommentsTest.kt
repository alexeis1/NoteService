package ru.netology.lesson8

import org.junit.Test

import org.junit.Assert.*

class NotesWithCommentsTest {

    @Test
    fun dropNoteComments() {

    }

    @Test
    fun createComment_succeeded() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id,  message = "comment1",
             guid = ("comment1").hashCode().toString())

        assertEquals(cid, 1)
    }

    @Test
    fun createComment_succeededCheckDuple() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id,  message = "comment1",
            guid = ("comment1").hashCode().toString())
                  notes.createComment(noteId = id,  message = "comment2",
            guid = ("comment2").hashCode().toString())

        assertEquals(cid, 1)
    }    

    @Test(expected = CommentAlreadyExistsException::class)
    fun createComment_failDuple() {
        val notes = NotesWithComments(1)
        val id = notes.add(title = "note1", text = "text note1")
        notes.createComment(noteId = id,  message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.createComment(noteId = id,  message = "comment2",
            guid = ("comment1").hashCode().toString())
    }

    @Test(expected = NoteNotFoundException::class)
    fun createComment_failNoteNotFound() {
        val notes = NotesWithComments(1)
        val id = notes.add(title = "note1", text = "text note1")
        notes.createComment(noteId = id + 1,  message = "comment1",
            guid = ("comment1").hashCode().toString())
    }

    @Test(expected = WrongNoteAuthorException::class)
    fun createComment_failWrongNoteAuthor() {
        val notes = NotesWithComments(1)
        val id = notes.add(title = "note1", text = "text note1")
        notes.createComment(noteId = id, ownerId = 2, message = "comment1",
            guid = ("comment1").hashCode().toString())
    }

    @Test(expected = ForbiddenToComment::class)
    fun createComment_forbiddenToComment() {
        //создаем с запретом комментировать
        val notes = NotesWithComments(1, false)
        val id = notes.add(title = "note1", text = "text note1")
        notes.createComment(noteId = id, ownerId = 2, message = "comment1",
            guid = ("comment1").hashCode().toString())
    }

    @Test
    fun deleteComment_succeeded() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        assertEquals(notes.deleteComment(commentId = cid), 1)
    }

    @Test(expected = WrongNoteAuthorException::class)
    fun deleteComment_failedWrongNoteAuthor() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid, 2)
    }

    @Test(expected = CommentNotFoundException::class)
    fun deleteComment_failedCommentNotFound() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid + 1)
    }

    @Test
    fun dropNoteComments_succeededDrop() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        assertEquals(notes.delete(id), 1)
    }

    @Test
    fun dropNoteComments_succeededNoCommentsId2() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val id2  = notes.add(title = "note2", text = "text note2")
         notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        assertEquals(notes.delete(id2), 1)
    }

    @Test
    fun dropNoteComments_succeededNoDeletedCommentsId2() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val id2  = notes.add(title = "note2", text = "text note2")
        val cid  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid)
        assertEquals(notes.delete(id2), 1)
    }

    @Test
    fun dropNoteComments_succeededDeleteDeleted() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid)
        assertEquals(notes.delete(id1), 1)
    }

    @Test
    fun editComment_succeeded() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        assertEquals(notes.editComment(commentId = cid, 1, "comment2"), 1)
    }

    @Test
    fun editComment_succeededDefault() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        assertEquals(notes.editComment(commentId = cid, message = "comment2"), 1)
    }

    @Test(expected = WrongNoteAuthorException::class)
    fun editComment_failedWrongNoteAuthor() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.editComment(commentId = cid, 2, "")
    }

    @Test(expected = CommentNotFoundException::class)
    fun editComment_failedWrongId() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.editComment(commentId = cid + 1, 1, "comment2")
    }


    @Test
    fun getComments_succeededDefault() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val cid1  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        val cid2  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment2").hashCode().toString())
        val comments = notes.getComments(id1)
        assertTrue(comments.size == 2 && comments[0].id == cid2 && comments[1].id == cid1)
    }

    @Test
    fun getComments_succeededEmpty() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment2").hashCode().toString())
        val comments = notes.getComments(noteId = id1, offset = 10)
        assertTrue(comments.isEmpty())
    }

    @Test
    fun getComments_succeededCorrectCount() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val cid1  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        val cid2  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment2").hashCode().toString())
        val comments = notes.getComments(noteId = id1, offset = 0, count = 2)
        assertTrue(comments.size == 2 && comments[0].id == cid2 && comments[1].id == cid1)
    }

    @Test
    fun getComments_succeededCorrectCountAscended() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val cid1  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        val cid2  = notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment2").hashCode().toString())
        val comments = notes.getComments(noteId = id1, offset = 0, count = 2, sort = true)
        assertTrue(comments.size == 2 && comments[0].id == cid1 && comments[1].id == cid2)
    }

    @Test
    fun getComments_succeededFilter() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val id2  = notes.add(title = "note2", text = "text note2")
                    notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        val cid2  = notes.createComment(noteId = id2, ownerId = 1, message = "comment2",
            guid = ("comment2").hashCode().toString())
        val comments = notes.getComments(noteId = id2, offset = 0, count = 2, sort = true)
        assertTrue(comments.size == 1 && comments[0].id == cid2)
    }

    @Test
    fun getComments_succeededFilterDec() {
        val notes = NotesWithComments(1)
        val id1  = notes.add(title = "note1", text = "text note1")
        val id2  = notes.add(title = "note2", text = "text note2")
                    notes.createComment(noteId = id1, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        val cid2  = notes.createComment(noteId = id2, ownerId = 1, message = "comment2",
            guid = ("comment2").hashCode().toString())
        val comments = notes.getComments(noteId = id2, offset = 0, count = 2, sort = false)
        assertTrue(comments.size == 1 && comments[0].id == cid2)
    }

    @Test
    fun restoreComment_succeeded() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid)
        assertEquals(notes.restoreComment(commentId = cid, 1), 1)
    }

    @Test(expected = WrongNoteAuthorException::class)
    fun restoreComment_failedWrongNoteAuthor() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid)
        notes.restoreComment(commentId = cid, 2)
    }

    @Test(expected = CommentNotFoundException::class)
    fun restoreComment_failedCommentNotFound() {
        val notes = NotesWithComments(1)
        val id  = notes.add(title = "note1", text = "text note1")
        val cid = notes.createComment(noteId = id, ownerId = 1, message = "comment1",
            guid = ("comment1").hashCode().toString())
        notes.deleteComment(commentId = cid)
        notes.restoreComment(commentId = cid+1, 1)
    }
}