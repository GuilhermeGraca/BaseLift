package com.example.baselift

import android.content.Context
import com.example.baselift.Model.local.AppDatabase

import com.example.baselift.Model.repository.UserRepository
import com.example.baselift.Model.repository.ProgressRepository

/**
 * Gere a injeção de dependências
 * Evita o uso de bibliotecas de injeção
 * Contentor central que instancia as dependências da aplicação
 *
 *  - É inicializado uma vez ao iniciar a app
 *  - Mantém referências globais como a base de dados
 *  - Expõe os repositórios e os DAOs necessários
 */
interface AppContainer {
    // Expõe a base de dados e os repositórios
    // para que possam ser injetados nas classes
    val database: AppDatabase
    val userRepository: UserRepository
    val progressRepository: ProgressRepository
    val workoutRepository: com.example.baselift.Model.repository.WorkoutRepository
}

/**
 * Implementação do AppContainer
 * Recebe o contexto e instancia as dependências
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    
    // lazy garante que a base de dados só é criada
    // na primeira vez que for necessária
    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    // Repositórios
    override val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    override val progressRepository: ProgressRepository by lazy {
        ProgressRepository(database.weightLogDao(), database.photoLogDao())
    }

    override val workoutRepository: com.example.baselift.Model.repository.WorkoutRepository by lazy {
        com.example.baselift.Model.repository.WorkoutRepository(database.workoutDao())
    }
}
