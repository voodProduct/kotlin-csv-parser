package ru.vood.kotlin.csv.parser.arrowExt

import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.RaiseDSL
import arrow.core.raise.accumulate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@RaiseDSL @OptIn(ExperimentalRaiseAccumulateApi::class, ExperimentalTypeInference::class, ExperimentalContracts::class)
@Suppress("WRONG_INVOCATION_KIND")
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    block: (A, B, C, D, E, F, G, H, I, J) -> K
): K {
    contract {
        callsInPlace(action1, EXACTLY_ONCE)
        callsInPlace(action2, EXACTLY_ONCE)
        callsInPlace(action3, EXACTLY_ONCE)
        callsInPlace(action4, EXACTLY_ONCE)
        callsInPlace(action5, EXACTLY_ONCE)
        callsInPlace(action6, EXACTLY_ONCE)
        callsInPlace(action7, EXACTLY_ONCE)
        callsInPlace(action8, EXACTLY_ONCE)
        callsInPlace(action9, EXACTLY_ONCE)
        callsInPlace(action10, EXACTLY_ONCE)
        callsInPlace(block, EXACTLY_ONCE)
    }
    return accumulate {
        val a = accumulating { action1() }
        val b = accumulating { action2() }
        val c = accumulating { action3() }
        val d = accumulating { action4() }
        val e = accumulating { action5() }
        val f = accumulating { action6() }
        val g = accumulating { action7() }
        val h = accumulating { action8() }
        val i = accumulating { action9() }
        val j = accumulating { action10() }
        block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value)
    }
}
