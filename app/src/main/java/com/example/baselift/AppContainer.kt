package com.example.baselift

import android.content.Context
import com.example.baselift.Model.local.AppDatabase

import com.example.baselift.Model.repository.UserRepository

/**
 * AppContainer é responsável pela Injeção de Dependências/Objetos Internos (DI)
 * Evita o uso desnecessário de lib de DI como Hilt ou Dagger
 * este contentor central que sabe como instanciar as dependências da aplicação
 *
 *  -É inicializado uma única vez quando a app é iniciada numa classe :Application customizada
 *  -Mantém referências globais partilhadas, como a BD
 *  -Expõe os Repositories, passa-lhes os DAOs necessários da BD
 */
interface AppContainer {
    // Expões a base de dados e os repositorios
    //para q possam ser injetados nas classes que a usam
    val database: AppDatabase
    val userRepository: UserRepository
}

/**
 * Implementação concreta do AppContainer
 * Recebe o Context (necessário para o Room) e instancia as dependências (lazy/quando necessário)
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    
    // 'lazy' garante que a BD só é criada ou recuperada
    // na primeira vez que for necessária
    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }
}
