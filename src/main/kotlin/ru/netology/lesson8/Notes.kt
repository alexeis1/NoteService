package ru.netology.lesson8

import sun.awt.Mutex
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

open class Notes(val userId: Int) {
    private val mutex               = Mutex()
    private var idGenerator         = AtomicInteger()
    private fun generateId() : Int  = idGenerator.incrementAndGet()
    protected var notes             = sortedMapOf<Int,Note>()
    protected open fun dropNoteComments(noteId : Int) {
        TODO("NotesWithComments implements")
    }

    /**
     * Создает новую заметку у текущего пользователя.
     * После успешного выполнения возвращает идентификатор созданной заметки (nid).
     */
    fun add(title : String, text : String,
            privacyView    : Array<String> = emptyArray(),
            privacyComment : Array<String> = emptyArray()) : Int
    {
        val id = generateId()
        mutex.lock()
            notes[id] = NoteData(note = Note(id = id, ownerId = userId, title = title, text = text),
                                 privacyView = privacyView, privacyComment = privacyComment)
        mutex.unlock()
        return id
    }

    /**
     * Удаляет заметку текущего пользователя.
     * Возваращает 1 в случае успеха
     */
    fun delete(noteId : Int) : Int
    {
        //удаляем безвозврато комментарии на заметку
        dropNoteComments(noteId)
        mutex.lock()
        try {
            //удаляем саму заметку
            notes.remove(noteId) ?: throw NoteNotFoundException(noteId)
            return 1
        }
        finally {
            mutex.unlock()
        }
    }

    /**
     * Редактирует заметку текущего пользователя.
     * Возваращает 1 в случае успеха
     */
    fun edit(noteId : Int, title : String, text : String,
             privacyView    : Array<String> = emptyArray(),
             privacyComment : Array<String> = emptyArray()) : Int
    {
        mutex.lock()
        try {
            val note = notes[noteId] ?: throw NoteNotFoundException(noteId)
            note as NoteData
            if (note.checkPrivacy(privacyView = privacyView, privacyComment = privacyComment))
            {
                notes[noteId] = note.copy(note.copy(title = title, text = text))
            }
            else throw InsufficientPermissionsForEdit(noteId)
        }
        finally {
            mutex.unlock()
        }
        return 1
    }

    /**
     * Возвращает список заметок, созданных пользователем.
     * noteIds - идентификаторы заметок, информацию о которых необходимо получить
     * userId  - идентификатор пользователя, информацию о заметках которого требуется получить
     * offset  - смещение, необходимое для выборки определенного подмножества заметок
     * count   - количество заметок, информацию о которых необходимо получит
     * sort    - сортировка результатов (0 — по дате создания в порядке убывания, 1 - по дате создания в порядке возрастания).
     */
    fun get(noteIds : Iterable<Int>, userId : Int = this.userId,
            offset :Int = 0, count : Int = 20, sort : Boolean = false) : List<Note>
    {
        mutex.lock()
            try{
                return if (sort)
                    notes.filterKeys{ noteIds.contains(it) }.flatMap { listOf(it.value) }.
                        sortedBy { (it as CommentData).date }.subList(offset, offset + count)

                else
                    notes.filterKeys{ noteIds.contains(it) }.flatMap { listOf(it.value) }.
                        sortedByDescending { (it as CommentData).date }.subList(offset, offset + count)
                         
            }
            finally {
                mutex.unlock()
            }
    }

    /**
     * Возвращает заметку по её id в виде списка ArrayList<GetIdNote>
     */
    fun getById(noteId : Int, ownerId : Int = this.userId, needWiki : Boolean = false) :
            ArrayList<GetIdNote>
    {
        mutex.lock()
        try {
            return arrayListOf(GetIdNote(notes[noteId] ?: throw NoteNotFoundException(noteId)))
        }
        finally {
            mutex.unlock()
        }
    }
}

class NoteNotFoundException         (id : Int) : RuntimeException("Note with id=$id not found")
class InsufficientPermissionsForEdit(id : Int) : RuntimeException("Insufficient Permissions for edit Note $id")

class NoteData(
        note           : Note,
    val privacyView    : Array<String> = emptyArray(),
    val privacyComment : Array<String> = emptyArray()
) : Note(
    id = note.id,
    ownerId = note.ownerId,
    comments = note.comments,
    date = note.date,
    title = note.title,
    text = note.text,
    canComment = note.canComment,
    viewUrl = note.viewUrl
)
{
    fun checkPrivacy(privacyView : Array<String>, privacyComment : Array<String>) : Boolean
    {
        return this.privacyView == privacyView && this.privacyComment == privacyComment
    }

    fun copy(note : Note = this,
             date           : Int = this.date,
             privacyView    : Array<String> = this.privacyComment,
             privacyComment : Array<String> = this.privacyComment): NoteData
    {
        return NoteData(note = note, privacyView = privacyView, privacyComment = privacyComment)
    }
}

class GetIdNote (
        note           : Note,
    val privacy        : Int = 0,
    val commentPrivacy : Int = 1
) : Note(
    id = note.id,
    ownerId = note.ownerId,
    comments = note.comments,
    date = note.date,
    title = note.title,
    text = note.text,
    canComment = note.canComment,
    viewUrl = note.viewUrl
)
