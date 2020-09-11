# Alan Seksenali
# Sphere: Artificial Intelligence
# Topic: finding a shortest path for a robot vacuum in an 8x8 grid board using either UCS or A* algorithm

def heuristic(pos, goal):
    # Compute the Chebyshev distance from current position to the goal:
    return max(abs(pos[0] - goal[0]),abs(pos[1] - goal[1]))

def get_neighbors(pos):
    neighbors = []
    for ix, iy in [(1,0),(-1,0),(0,1),(0,-1),(1,1),(-1,1),(1,-1),(-1,-1)]:
        nx = pos[0] + ix
        ny = pos[1] + iy
        if nx < 1 or nx > 8 or ny < 1 or ny > 8:
            continue
        neighbors.append((nx,ny))
    return neighbors

def move_cost(next_node, barrier):
    for bar_pos in barrier:
        if next_node in bar_pos:
            return 200
    return 1 

def UCSearch(start, goal, barrier):
    # Firstly, create a dictonary that stores the path cost so far to reach each node from 
    # the start position. For example, G ={(3, 3): 2} depicts that path cost of (3,3) is 2:
    G = {} 

    #Initialize value at starting position:
    G[start] = 0

    # Create two sets. One set (i.e. expandedNodes) is used to store the expanded nodes, 
    # and another set (i.e. fringe) is to store nodes on the fringe. With this data structure,
    # you can use add() (e.g. fringe.add(node)) or remove() (e.g. fringe.remove(node)) functions
    # to update nodes on the given set:

    expandedNodes = set() 
    fringe = set([start]) 

    # Create a dictonary that keeps track of parents of the opened nodes on the path to the goal.
    # For example, parent = {(1, 2): (1, 1)} depicts that (1,1) is a parent of (1,2).
    # This data structure can help to retrace the path when we reach to the goal:
    parent = {} 

    while len(fringe) > 0:
        #Pick the node in the fringe with the lowest G score
        curNode = None # initialize the current node.
        curGscore = None # initialize the G score of current node

        for pos in fringe:
            if curNode is None or G[pos] < curGscore:
                curGscore = G[pos]
                curNode = pos
        fringe.remove(curNode)

        #Check if our robot has reached the goal
        if curNode == goal:
            #Retrace our path backward
            path = [curNode]
            while curNode in parent:
                curNode = parent[curNode]
                path.append(curNode)
            path.reverse()
            return path, expandedNodes, G[goal] #Done!
        
        # Marking the current node as visited (expanded):
        expandedNodes.add(curNode)
        # Storing the neighbors of current node in a separate list:
        neighbors = get_neighbors(curNode)
        # The loop through each of the neighbors of the current node:
        for neighbor in neighbors:
            # Calculating the cost of the movement to the current neighbor:
            pathCost = move_cost(neighbor, barrier)
            # Checking whether this neighbor has not been expanded, and if it is optimal:
            if (neighbor not in G and neighbor not in expandedNodes) or (pathCost + G[curNode] < G[neighbor]):
                # Updating the value (cost) of the path of this neighbor:
                G[neighbor] = pathCost + G[curNode]
                # Updating the parent of the neighbor as the current node:
                parent[neighbor] = curNode
            # Checking if it a new (not expanded) neighbor:
            if neighbor not in expandedNodes:
                # In this case, a new (not expanded) neighbor is added to the fringe list:
                fringe.add(neighbor)

    raise RuntimeError("UCS failed to find a solution")

def aStarSearch(start, goal, barrier):
    # Firstly, create two dictonaries that store the path cost (g) and total estimated cost (f)
    # to reach each node from the start position. 
    # For example, F ={(3, 3): 7} depicts that total cost of (3,3) is 7.
    G = {} 
    F = {} 

    # Initialize the values at starting position:
    G[start] = 0
    F[start] = heuristic(start, goal)

    # Create two sets. One set (i.e. expandedNodes) is used to store the expanded nodes, 
    # and another set (i.e. fringe) is to store nodes on the fringe. With this data structure,
    # you can use add() (e.g. fringe.add(node)) or remove() (e.g. fringe.remove(node)) functions
    # to update nodes on the given set.
    expandedNodes = set() 
    fringe = set([start]) 

    # create a dictonary that keeps track of parents of the opened nodes on the path to the goal.
    # For example, parent ={(1, 2): (1, 1)} depicts that (1,1) is a parent of (1,2).
    # This data structure can help to retrace the path when we reach to the goal.
    parent = {} 

    while len(fringe) > 0:
        # Pick the node in the fringe with the lowest F score
        curNode = None # initialize the current node.
        curFscore = None # initialize the F score of current node
        
        for pos in fringe:
            if curNode is None or F[pos] < curFscore:
                curFscore = F[pos]
                curNode = pos
        fringe.remove(curNode)
        
        #Check if our robot has reached the goal
        if curNode == goal:
            #Retrace our path backward
            path = [curNode]
            while curNode in parent:
                curNode = parent[curNode]
                path.append(curNode)
            path.reverse()
            return path, expandedNodes, F[goal] #Done!

        # Marking the current node as visited (expanded):
        expandedNodes.add(curNode)
        # Storing the neighbors of current node in a separate list:
        neighbors = get_neighbors(curNode)
        # The loop through each of the neighbors of the current node:
        for neighbor in neighbors:
            # Calculating the cost of the movement to the current neighbor:
            pathCost = move_cost(neighbor, barrier)
            # Checking if the neighbor does not present in F or if it is needed to update the total cost of current neighbor:
            if neighbor not in F or F[neighbor] > G[curNode] + pathCost + heuristic(neighbor, goal):
                # Updating the total cost of the current neighbor:
                F[neighbor] = G[curNode] + pathCost + heuristic(neighbor, goal)
                # Updating the path cost of the current neighbor:
                G[neighbor] = pathCost + G[curNode]
                # Checking if it a new (not expanded) neighbor:
                if neighbor not in expandedNodes:
                    # In this case, a new (not expanded) neighbor is added to the fringe list:
                    fringe.add(neighbor)

    raise RuntimeError("A* failed to find a solution")