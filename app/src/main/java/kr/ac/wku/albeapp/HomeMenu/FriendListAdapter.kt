package kr.ac.wku.albeapp.HomeMenu

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kr.ac.wku.albeapp.R
import kr.ac.wku.albeapp.logins.UserStatus

class FriendListAdapter(private val friendList: List<Friendlist.Friend>) :
    RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.home_profileimage)
        val userName: TextView = itemView.findViewById(R.id.home_username)
        val userPhoneNumber: TextView = itemView.findViewById(R.id.home_userphonenumber)
        val userStatus: ImageView = itemView.findViewById(R.id.home_userstatus)
        val userStatusText: TextView = itemView.findViewById(R.id.home_userstatus_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friendlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val friend = friendList[position]
        val phoneNumber = friend.userID
        println("바인딩 데이타: $friend")

        val imageRef = storage.getReference().child("image/$phoneNumber")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.itemView.context)
                .load(uri) // 친구의 이미지 URL을 사용합니다.
                .into(holder.profileImage)
        }.addOnFailureListener {
            // 이미지 로드에 실패했을 때 기본 이미지를 설정합니다.
            holder.profileImage.setImageResource(R.drawable.base_profile_image)
        }

        holder.userName.text = friend.userName
        // 번호가 없어도 일단 나오게 수정
        holder.userPhoneNumber.text = friend.userID ?: "번호 없음"

        Log.d("FriendListAdapter", "userState: ${friend.userState}")

        val userStatus = UserStatus.fromStatus(friend.userState ?: 0)
        holder.userStatusText.text = userStatus.description

        // 유저 상태 보고 이미지 결정
        when (userStatus) {
            UserStatus.ACTIVE -> holder.userStatus.setImageResource(R.drawable.check)
            UserStatus.INACTIVE -> holder.userStatus.setImageResource(R.drawable.noinfo)
            UserStatus.TEMP_INACTIVE -> holder.userStatus.setImageResource(R.drawable.away)
            else -> holder.userStatus.setImageResource(R.drawable.nothing)
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}