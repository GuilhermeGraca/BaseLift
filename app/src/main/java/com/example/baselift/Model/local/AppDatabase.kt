package com.example.baselift.Model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.baselift.Model.local.entity.UserEntity
import com.example.baselift.Model.local.dao.UserDao
import com.example.baselift.Model.local.entity.WeightLogEntity
import com.example.baselift.Model.local.dao.WeightLogDao
import com.example.baselift.Model.local.entity.PhotoLogEntity
import com.example.baselift.Model.local.dao.PhotoLogDao


/**
 * Base de dados principal da BaseLift
 *
 * Utiliza o padrão Singleton - garante q existe apenas uma instância
 * da base de dados em toda a aplicação - evita q duas threads acessem/modifiquem o mesmo recurso (race conditions)
 * e desperdício de recursos
 */
@Database(
    entities = [UserEntity::class, WeightLogEntity::class, PhotoLogEntity::class], //a lista de "tabelas" que a base de dados vai ter
                   //Cada entity é uma data class anotada com @Entity

    version = 3, //o número da versão do esquema da base de dados.
                 //Sempre que adicionarmos ou alterarmos uma tabela temos de
                 //incrementar este número para n dar erro
                 //serve para o Room saber que precisa de fazer uma "migração" dos dados antigos

    exportSchema = false //true - exporta o esquema da base de dados para um ficheiro JSON
                         //fica a false pq n precisamos dessa funcionalidade e assim não se tem q configurar a pasta de destino
)

// abstract pq o Room - lib da google com SQlite - vai gerar automaticamente
// a implementação concreta em runtime via KSP
// só declaramos a "forma"
abstract class AppDatabase : RoomDatabase() {

    // declarações dos DAOs como fun abstract
    // Dao é a interface que define as queries à base de dados (SELECT, INSERT, etc.).
    // O Room implementa-os automaticamente
    abstract fun userDao(): UserDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun photoLogDao(): PhotoLogDao


    // companion object / static obj para implementar o singleton
    companion object {

        // @Volatile garante que o valor de INSTANCE é sempre lido e escrito
        // diretamente na memória principal em vez de cache local de thread
        // Se não, duas threads poderiam ver valores diferentes de INSTANCE ao msm tempo
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // Se já existir uma instância, devolve-a logo
            return INSTANCE ?: synchronized(this) {
                // synchronized(this) garante que só uma thread de cada vez
                // pode entrar neste bloco
                val instance = Room.databaseBuilder(
                    context.applicationContext, // applicationContext para evitar memory leaks
                    AppDatabase::class.java,    // classe da nossa base de dados
                    "baselift_database"         // nome do ficheiro .db no dispositivo
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
