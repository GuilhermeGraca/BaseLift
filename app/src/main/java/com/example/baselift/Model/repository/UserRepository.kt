package com.example.baselift.Model.repository

import com.example.baselift.Model.local.dao.UserDao
import com.example.baselift.Model.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Abstração entre o UserDao e os ViewModels
 * faz a ligação entre a base de dados e as views
 */
class UserRepository(private val userDao: UserDao) {

    // retorna um Flow contínuo com os dados do utilizador
    // sempre que o UserEntity for alterado os ViewModels
    // recebem a atualização automaticamente
    fun getUser(): Flow<UserEntity?> {
        return userDao.getUser()
    }

    // função para inserir ou atualizar o utilizador
    suspend fun saveUser(user: UserEntity) {
        userDao.insertOrUpdateUser(user)
    }

    suspend fun clearUserData() {
        userDao.clearUserTable()
    }
}
