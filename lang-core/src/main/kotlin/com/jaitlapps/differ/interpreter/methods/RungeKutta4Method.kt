package com.jaitlapps.differ.interpreter.methods

import java.util.*

class RungeKutta4Method : AbstractMethod() {
    override fun calculate(coeff: Map<String, Double>, xk: Map<String, Double>, startInterval: Double, endInterval: Double, step: Double, expressions: Map<String, String>): MethodResult {
        val currentXk = HashMap<String, ArrayList<Double>>()
        for((x,k) in xk) {
            val array = ArrayList<Double>()
            array.add(k)
            currentXk.put(x, array)
        }

        val steps = ArrayList<Double>()
        var current = startInterval
        do {
            steps.add(current)
            current += step
        } while(current <= endInterval + step)

        var pos = 1
        for (st in steps.subList(1, steps.size)) {
            for ((diff, exp) in expressions) {
                val diffx = diff.replace("dxdt", "x")
                val mapParams = HashMap<String, Double>()
                mapParams.putAll(coeff)
                mapParams.put("h", st)
                mapParams.putAll(getCurrentXk(currentXk, pos))

                val bExp = "h * ($exp)"

                val curDiffxVal = mapParams.get(diffx)!!

                val k1 = evaluateExpression(mapParams, bExp)

                mapParams.put(diffx, curDiffxVal + (k1/2.0))

                val k2 = evaluateExpression(mapParams, bExp)

                mapParams.put(diffx, curDiffxVal + (k2/2.0))

                val k3 = evaluateExpression(mapParams, bExp)

                mapParams.put(diffx, curDiffxVal + k2)

                val k4 = evaluateExpression(mapParams, bExp)

                val delta = 1.0/6.0 * (k1 + 2.0*k2 + 2.0*k3 + k4)

                val resultVal = curDiffxVal + delta

                saveCurrentXk(currentXk, diffx, resultVal)
            }
            pos += 1
        }

        return createResult(steps, currentXk);
    }
}