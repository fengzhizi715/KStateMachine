package com.safframework.statemachine.v2.algorithm

import com.safframework.statemachine.v2.state.IState
import com.safframework.statemachine.v2.state.InternalState
import com.safframework.statemachine.v2.state.requireParent

/**
 *
 * @FileName:
 *          com.safframework.statemachine.v2.algorithm.TreeAlgorithm
 * @author: Tony Shen
 * @date: 2023/7/4 14:58
 * @version: V1.0 <描述当前版本功能>
 */
object TreeAlgorithm{

    fun InternalState.findPathFromTargetToLca(targetState: InternalState): MutableList<InternalState> {
        var thisNode = this
        var targetNode = targetState
        var thisDepth = thisNode.findDepth()
        var targetDepth = targetState.findDepth()
        val targetPath = mutableListOf<InternalState>()

        while (thisDepth != targetDepth) {
            if (thisDepth > targetDepth) {
                thisNode = thisNode.requireParent()
                thisDepth--
            } else {
                targetPath.add(targetNode)

                targetNode = targetNode.requireParent()
                targetDepth--
            }
        }

        while (thisNode !== targetNode) {
            thisNode = thisNode.requireParent()

            targetPath.add(targetNode)
            targetNode = targetNode.requireParent()
        }

        targetPath.add(thisNode) // add lca
        return targetPath
    }

    private fun IState.findDepth(): Int {
        var depth = 0
        var parent = this.parent
        while (parent != null) {
            depth++
            parent = parent.parent
        }
        return depth
    }
}