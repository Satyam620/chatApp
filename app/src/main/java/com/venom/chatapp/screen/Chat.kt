package com.venom.chatapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.chatapp.R
import com.venom.chatapp.data.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.venom.chatapp.viewmodel.MessageViewModel
import com.venom.chatapp.viewmodel.RoomViewModel



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    roomId: String,
    messageViewModel: MessageViewModel = viewModel(),
) {
    val messages by messageViewModel.messages.observeAsState(emptyList())
    messageViewModel.setRoomId(roomId)
    val text = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) {message ->
                ChatMessageItem(
                    message = message.copy(
                        isSentByCurrentUser =
                        message.senderId == messageViewModel.currentUser.value?.email
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            IconButton(onClick = {
                if (text.value.isNotEmpty()) {
                    messageViewModel.sendMessage(text.value.trim())
                    text.value = ""
                }
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()

    return when {
        isSameDay(messageDateTime, now) -> "today ${formatTime(messageDateTime)}"
        isSameDay(messageDateTime.plusDays(1), now) -> "yesterday ${formatTime(messageDateTime)}"
        else -> formatDate(messageDateTime)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
        Text(
            text = formatTimestamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}

data class Room(
    val id: String = "",
    val name: String = ""
)

@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    onJoinClicked: (Room) -> Unit
) {
    val rooms by roomViewModel.rooms.observeAsState(emptyList())
    var showDialog by remember {
        mutableStateOf(false)
    }
    var name by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Chat Rooms", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(rooms) { room ->
                RoomItem(room = room, onJoinClicked = {onJoinClicked(room)})
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Create Room")
        }

        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = true },
                title = { Text("Create a new room") },
                text = {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }, confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    showDialog = false
                                    roomViewModel.createRoom(name)

                                }
                            }
                        ) {
                            Text("Add")
                        }
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun RoomItem(room: Room,onJoinClicked: (Room) ->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = room.name, fontSize = 16.sp, fontWeight = FontWeight.Normal)
        OutlinedButton(onClick = { onJoinClicked(room)}) {
            Text(text = "Join")
        }
    }
}
