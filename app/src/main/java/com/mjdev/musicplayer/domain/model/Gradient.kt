package com.mjdev.musicplayer.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.mjdev.musicplayer.presentation.ui.theme.Exotic1
import com.mjdev.musicplayer.presentation.ui.theme.Exotic2
import com.mjdev.musicplayer.presentation.ui.theme.LusciousLime1
import com.mjdev.musicplayer.presentation.ui.theme.LusciousLime2
import com.mjdev.musicplayer.presentation.ui.theme.OceanBlue1
import com.mjdev.musicplayer.presentation.ui.theme.OceanBlue2
import com.mjdev.musicplayer.presentation.ui.theme.PurpleLake1
import com.mjdev.musicplayer.presentation.ui.theme.PurpleLake2
import com.mjdev.musicplayer.presentation.ui.theme.Quepal1
import com.mjdev.musicplayer.presentation.ui.theme.Quepal2
import com.mjdev.musicplayer.presentation.ui.theme.Sanguine1
import com.mjdev.musicplayer.presentation.ui.theme.Sanguine2
import com.mjdev.musicplayer.presentation.ui.theme.SweetMorning1
import com.mjdev.musicplayer.presentation.ui.theme.SweetMorning2
import com.mjdev.musicplayer.presentation.ui.theme.Yosemite1
import com.mjdev.musicplayer.presentation.ui.theme.Yosemite2

enum class Gradient(val firstColor:Int,val secondColor :Int){
    None(-1,-1),
    Sanguine(Sanguine1.toArgb(),Sanguine2.toArgb()),
    LusciousLime(LusciousLime1.toArgb(),LusciousLime2.toArgb()),
    PurpleLake(PurpleLake1.toArgb(), PurpleLake2.toArgb()),
    Exotic(Exotic1.toArgb(), Exotic2.toArgb()),
    Yosemite(Yosemite1.toArgb(),Yosemite2.toArgb()),
    SweetMorning(SweetMorning1.toArgb(), SweetMorning2.toArgb()),
    Celestial(Celestial1.toArgb(), Celestial2.toArgb()),
    MountainRock(MountainRock1.toArgb(), MountainRock2.toArgb()),
    PlumPlate(PlumPlate1.toArgb(), PlumPlate2.toArgb()),
    Quepal(Quepal1.toArgb(), Quepal2.toArgb());
    companion object{
        fun valueOf(ordinal : Int?) : Gradient? {
           return if (ordinal == null)
                None
            else {
                Gradient.entries.firstOrNull { it.ordinal == ordinal }
            }
        }
    }
}

val Toxic1 = Color(0xFFBFF098)
val Toxic2 = Color(0xFF6FD6FF)


val Orbit1 = Color(0xFF92EFFD)
val Orbit2 = Color(0xFF4E65FF)


val NoMans1= Color(0xFFA9F1DF)
val NoMans2 = Color(0xFFFFBBBB)

val Celestial1 = Color(0xFFC33764)
val Celestial2 = Color(0xFF1D2671)

val MountainRock1= Color(0xFF868F96)
val MountainRock2 = Color(0xFF596164)

val JuicyPeach1= Color(0xFFFFECD2)
val JuicyPeach2 = Color(0xFFFCB69F)

val WinterNeva1= Color(0xFFC2E9FB)
val WinterNeva2 = Color(0xFFA1C4FD)

val PlumPlate1= Color(0xFF667EEA)
val PlumPlate2 = Color(0xFF764BA2)



