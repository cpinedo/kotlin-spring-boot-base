package es.cpinedo.kotlinbase.security.application.data

import java.util.UUID

class UserData (val id: UUID, val alias:String, val email:String, val roles:Set<String>, val erased:Boolean){
    override fun toString(): String {
        return "UserData(id=$id, alias='$alias', email='$email', roles=$roles, erased=$erased)"
    }
}
