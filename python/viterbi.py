'''
Created on Mar 21, 2011

@author: shandy
@see: http://en.wikipedia.org/wiki/Viterbi_algorithm

STATES = ('Rainy', 'Sunny')
OBSERVATIONS = ('walk', 'shop', 'clean')
START_PROBABILITY = {'Rainy': 0.6, 'Sunny': 0.4}
TRANSITION_PROBABILITY = {
   'Rainy' : {'Rainy': 0.7, 'Sunny': 0.3},
   'Sunny' : {'Rainy': 0.4, 'Sunny': 0.6}
   }
EMISSION_PROBABILITY = {
   'Rainy' : {'walk': 0.1, 'shop': 0.4, 'clean': 0.5},
   'Sunny' : {'walk': 0.6, 'shop': 0.3, 'clean': 0.1}
   }
'''

STATES = ('S1', 'S2')

OBSERVATIONS = ('A', 'G', 'A', 'G', 'A')

START_PROBABILITY = {'S1': 0.5, 'S2': 0.5}

TRANSITION_PROBABILITY = {
   'S1' : {'S1': 0.4, 'S2': 0.6},
   'S2' : {'S1': 0.25, 'S2': 0.75}
   }

EMISSION_PROBABILITY = {
   'S1' : {'A': 0.5, 'G': 0.5},
   'S2' : {'A': 0.9, 'G': 0.1}
   }

PRINT_CALCS = True
PRINT_PATHS = False
CALC_INDENT = "         = "


# Helps visualize the steps of Viterbi.
def print_dptable(V):
    print "    ",
    for i in range(len(V)): print "%7s" % ("%d" % i),
    print

    for y in sorted(V[0].keys()):
        print "%.5s: " % y,
        for t in range(len(V)):
            print "%.7s" % ("%f" % V[t][y]),
        print


def viterbi(obs, states, start_p, trans_p, emit_p):
    V = [{}]
    path = {}

    # Initialize base cases (t == 0)
    for k in states:
        V[0][k] = start_p[k] * emit_p[k][obs[0]]
        path[k] = [k]

        if PRINT_CALCS:
            print "V[0][" + k + "] = " + str(emit_p[k][obs[0]]) + " * " + str(start_p[k]) + " = " + str(V[0][k])
            if PRINT_PATHS:  print "path[" + k + "] = " + str(path[k])
            print

    # Run Viterbi for t > 0
    for t in range(1,len(obs)):
        V.append({})
        newpath = {}

        for k in states:
            (prob, state) = max([(V[t-1][y] * trans_p[y][k] * emit_p[k][obs[t]], y) for y in states])
            V[t][k] = prob
            newpath[k] = path[state] + [k]

            if PRINT_CALCS:
                print "V[" + str(t) + "][" + k + "] = max(",
                for y in states:  print "a[" + y + "][" + k + "]*V[" + str(t-1) + "][" + y + "]*e[" + k + "][" + str(obs[t]) + "],",
                print ")"
                print CALC_INDENT + "max(",
                for y in states:  print str(trans_p[y][k]) + "*" + str(V[t-1][y]) + "*" + str(emit_p[k][obs[t]]) + ",",
                print ")"
                print CALC_INDENT + "max(",
                for y in states:  print str(trans_p[y][k] * V[t-1][y] * emit_p[k][obs[t]]) + ",",
                print ")"
                print CALC_INDENT + str(V[t][k])
                if PRINT_PATHS:  print "path[" + k + "] = " + str(newpath[k])
                print

        # Don't need to remember the old paths
        path = newpath

    print
    print_dptable(V)
    print
    (prob, state) = max([(V[len(obs) - 1][k], k) for k in states])
    return (prob, path[state])


def example():
    return viterbi(OBSERVATIONS, STATES, START_PROBABILITY, TRANSITION_PROBABILITY, EMISSION_PROBABILITY)


# main
print example()
