package com.example.baselift.Model.repository

import com.example.baselift.Model.local.dao.UserDao
import com.example.baselift.Model.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserRepository é a abstracao entre o UserDao e os ViewModels
 * ele faz a ligação entre o DAO e as Views
 */
class UserRepository(private val userDao: UserDao) {

    // Retorna um Flow contínuo com os dados do user
    // Sempre que o UserEntity for alterado na BD, os observadores/ViewModels
    // recebem a atualização automaticamente
    fun getUser(): Flow<UserEntity?> {
        return userDao.getUser()
    }

    // Função suspend / coroutine para inserir ou atualizar o utilizador
    suspend fun saveUser(user: UserEntity) {
        userDao.insertOrUpdateUser(user)
    }

    suspend fun clearUserData() {
        userDao.clearUserTable()
    }
}
