import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.phonebooktestapp.storage.table.ContactsTable
import com.example.phonebooktestapp.storage.ContactDatabase
import com.example.phonebooktestapp.storage.dao.ContactsDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var contactDao: ContactsDao
    private lateinit var db: ContactDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        contactDao = db.contactsDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun etWord() = runBlocking {
        val word = ContactsTable()
        contactDao.insert(word)
        val ContactOne = contactDao.getToContact()
        assertEquals(ContactOne?.phone, -1)
    }

}
