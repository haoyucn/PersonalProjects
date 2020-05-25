# --------------------------------------------------------------------
# this code is meant to identify grammar in a sequence, implementing the algorithm from the paper below
# it contains 2 sections:
#       1): parse the grammar from sequence
#       2): use grammar to generate sequence
# created by Hao Yu 3/20/2020
# original paper: Identifying Hierarchical Structure in Sequences -- C. G. Nevill-Manning
# link: https://www.jair.org/index.php/jair/article/view/10192
#---------------------------------------------------------------------
import copy
import uuid

# -------------------------------------------------------------------- section start
# this section is to find the grammar
# --------------------------------------------------------------------


# parse grammar
# input: an array 
# output: rules 
#           for example: if I have a rule like S = a, B, e     B = c, d
#           my rules are like [[S, [a, B, e]] , [B, [c, d]], ....]
def parse_grammar(input):
    rules = []
    changed = 1
    while changed:
        changed = 0
        input = turn_into_pairs(input)
        # find the duplication list and the appearing(occ:occurenting) position in the lane (occ)
        occ, duplist = find_occurance(input)
        # check if their is any duplications
        if len(duplist) > 0:
            changed = 1 # stay in the loop

        # dont really need a for loop, it will restart from beginning once the first pair of duplication is replaced with a non-terminal
        for i in range(len(duplist)):
            f, l = duplist[i]

            if len(occ[f][l]) > 0:
                # get a new non-terminal symbol
                ns = get_new_symbol()
            for k in range(len(occ[f][l])):
                index = occ[f][l][k]
                if index > 0:
                    # check if the previous pair has already been replaced
                    if len(input[index-1]) != 1:
                        # if not replace the symbol
                        input[index - 1][1] = ns
                input[index] = [ns]
                # check if out of bound
                if index < (len(input) - 1):
                    input[index + 1][0] = ns
            occ[f][l] = []
            rules.append([ns, [f,l]])
            break
        input = unfold(input)
    return [["S", input]] + rules

# turn [a, b, c ,d ] into [[a,b],[b,c],[c,d]]
# input: input = [a, b, c, d, ...]
# output: [[a,b],[b,c],[c,d]]
def turn_into_pairs(input):
    pairs = []
    b = input[0]
    for i in range(1,len(input)):
        pairs.append([b,input[i]])
        b = input[i]
    # print(pairs)
    return pairs

# turn [[a,b],[b,c],[c,d]] into [a, b, c ,d ]
# input: input = [[a,b],[b,c],[c,d],...] 
# output: [a, b, c, d, ...]
def unfold(pairs):
    new_input = [pairs[0][0]]
    for i in range(len(pairs)):
        if len(pairs[i]) == 1:
            continue
        else:
            new_input.append(pairs[i][1])
    return new_input

# find the duplicated pairs and its location
# input: [[a,b],[b,c],[c,d],..., [a , b], ...]
# output: occ = {{a, [0, index]}, ... }
#         duplist = [[a,b], ... ]
def find_occurance(input):
    occ = {}
    duplist = []
    for i in range(len(input)):
        f = input[i][0]
        l = input[i][1]
        # if it is already in the occerance list
        if f in occ.keys():
            if l in occ[f].keys():
                # put the pair in duplist and record position
                occ[f][l].append(i)
                duplist.append([f, l])
            else:
                occ[f][l] = [i]
        else:
            # put create the pair position
            occ[f] = {}
            occ[f][l] = [i]
    return occ, duplist

# get uuid symbol to aviod duplication
def get_new_symbol():
    return uuid.uuid1()

# remove the rules that is only used once
# input: rules = [[non-terminal, [expression_index_0, expression_index_1, ... ]], ...]
# output: rules but shortened
def shorten_rules(rules):
    termainals  = []
    i = 0
    while i < len(rules):
        nterminal = rules[i][0]
        # if number of times it appear is 1
        if num_appeared(rules, nterminal) == 1:
            remove_rules(rules, nterminal)
            rules = rules[0:i] + rules[i+1:]
        else:
            i = i +1
    return rules

# count how many unique rules using this non-terminal 
def num_appeared(rules, terminals):
    appearance = 0
    for i in range(len(rules)):
        for j in range(len(rules[i][1])):
            if rules[i][1][j] == terminals:
                appearance = appearance + 1
                continue
    return appearance
        
# remove a rule, represented by the non-terminal, from the rules
# input: rules, non terminal
# output: rules
def remove_rules(rules, nterminal):
    # get the expression
    expression = []
    for i in range(len(rules)):
        if rules[i][0] == nterminal:
            expression = rules[i][1]
            break
    
    for i in range(len(rules)):
        # loop through each rules expression to find the nonterminal
        j = 0
        while j < len(rules[i][1]):
            if rules[i][1][j] == nterminal:
                # remove the non terminal, and insert the expression
                rules[i][1] = rules[i][1][0:j] + expression + rules[i][1][j+1:]
                print(rules[i][1])
                j = j + len(expression)
            else:
                j = j + 1


# ------------------------------------------------------------------------------------
# this is how you use the functions above to parse a grammar
# input: a file contains input symbol one per row
#        example:   a \n b \n c \n
def run_parse_grammar(fileName):
    file = open(fileName)
    input = []
    lines = file.readlines()
    for i in range(len(lines)):
        input.append(lines[i].strip())

    rules = parse_grammar(input)
    rules = shorten_rules(rules)
    print(len(rules))
#--------------------------------------------------------------------------section end
        

# ------------------------------------------------------------------------- section start
# this section is how to use grammar to generate output
# --------------------------------------------------------------------------

# start with the begining symbol, and replace all nonterminal with the coordinated expression, until there is no expression left
# input; grammar = it is the rules above
# output: expressed array
def generate_generation(grammar):
    result = []
    S = grammar[0][1]
    for t in S:
        result.extend(generate_by_terminal(grammar, t))
    return result

# the recursive version of generate_generation(grammar)
# input: grammar
#        terminal that need to be interpreted
# putput: the interpretation of the terminal which contains no non-terminals
def generate_by_terminal(grammar, terminal):
    array_to_exploit = []
    found  = False
    for i in range(len(grammar)):
        if grammar[i][0] == terminal:
            array_to_exploit = grammar[i][1]
            found = True
           
            break
    
    if not found:
        return [terminal]
    result = []
    for t in array_to_exploit:
        generated_str = generate_by_terminal(grammar, t)
        result.extend(generated_str)
    return result


# this is how you use the section above
def run_generate(rules):
    generation = generate_generation(rules)
    for i in generation:
        print(i)
#--------------------------------------------------------------------------section end