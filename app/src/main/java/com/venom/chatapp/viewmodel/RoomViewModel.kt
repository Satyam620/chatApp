package com.venom.chatapp.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.chatapp.data.RoomRepository
import com.venom.chatapp.screen.Room
import com.venom.chatapp.data.Result.Success
import com.venom.chatapp.data.Result.Error
import com.venom.chatapp.Injection
import kotlinx.coroutines.launch

class RoomViewModel: ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms
    private val roomRepository: RoomRepository = RoomRepository(Injection.instance())

    init {
        loadRooms()
    }

    fun createRoom(name: String){
        viewModelScope.launch {
            roomRepository.createRoom(name)
        }
    }


    fun loadRooms() {
        viewModelScope.launch {
            when(val result = roomRepository.getRooms()){
                is Success -> _rooms.value = result.data!!
                is Error->{

                }
            }
        }
    }


}