import com.github.ekenstein.sulfurian.Config
import com.github.ekenstein.sulfurian.ServiceContext
import com.github.ekenstein.sulfurian.database.createSchema
import com.github.ekenstein.sulfurian.http.Server
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    val config = Config.parse(args)
    val database = Database.connect("jdbc:sqlite:local.db", "org.sqlite.JDBC")
    createSchema(database)

    Server.start(config)
}
