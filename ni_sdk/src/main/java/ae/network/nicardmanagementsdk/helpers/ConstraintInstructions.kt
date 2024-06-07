package ae.network.nicardmanagementsdk.helpers

import ae.network.nicardmanagementsdk.api.models.input.CardElementLayout
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updatePadding

/*
<ImageView
            android:id="@+id/image"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/title" />

app:layout_constraintTop_toBottomOf=”@id/title”
||
ConnectConstraint(R.id.image, ConstraintSet.Top, R.id.title, ConstraintSet.BOTTOM)

val imageView = ImageView(context)
imageView.id = View.generateViewId()
imageView.setImageResource(resId)
constraintLayout.addView(imageView)
val set = ConstraintSet()
set.clone(constraintLayout)
set.connect(imageView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
set.applyTo(constraintLayout)
* */
interface ConstraintInstructions
data class ConnectConstraint(val startID: Int, val startSide: Int, val endID: Int, val endSide: Int) : ConstraintInstructions
data class DisconnectConstraint(val startID: Int, val startSide: Int) : ConstraintInstructions
fun ConstraintLayout.updateConstraints(instructions: List<ConstraintInstructions>) {
    ConstraintSet().also {
        it.clone(this)
        for (instruction in instructions) {
            if (instruction is ConnectConstraint) it.connect(instruction.startID, instruction.startSide, instruction.endID, instruction.endSide)
            if (instruction is DisconnectConstraint) it.clear(instruction.startID, instruction.startSide)
        }
        it.applyTo(this)
    }
}
fun ConstraintLayout.clearConstraints(viewID: Int) {
    ConstraintSet().also {
        it.clone(this)
        it.clear(viewID)
        it.applyTo(this)
    }
}

private fun View.setConstraints(position: CardElementLayout, constraintLayout: ConstraintLayout) {
    if (position.left == null && position.right == null && position.top == null && position.bottom == null) {
        return
    }
    val viewId = this.id
    constraintLayout.updateConstraints(
        listOf(
        // By default every view has top-left constraints - clear it
        DisconnectConstraint(viewId, ConstraintSet.START), DisconnectConstraint(viewId, ConstraintSet.TOP)
    )
    )

    val instructions: MutableList<ConstraintInstructions> = mutableListOf()

    position.left?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START))
        this.updatePadding(left = it)
    }
    position.top?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP))
        this.updatePadding(top = it)
    }
    position.bottom?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM))
        this.updatePadding(top = it)
    }
    position.right?.let { it ->
        instructions.add(ConnectConstraint(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END))
        //this.setMargins(right = it)
        this.updatePadding(right = it)
    }
    constraintLayout.updateConstraints(instructions)
}
