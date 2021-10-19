import os
basicPath = "./TripAdvisorScrapper/TripAdvisorData/Sao_Paulo"
restaurantDict = [[i.split('#')[0]+'|',i.split('.csv')[0]+'|'] for i in os.listdir(basicPath) if '#' in i]
restaurantLists = [i for i in os.listdir(basicPath) if not '#' in i]

for list in restaurantLists:
    with open(basicPath+'/'+list,'r') as f:
        lines = f.readlines()
    
    for i in range(1,len(lines)):
        for restaurant in restaurantDict:
            print(restaurant[0]+'//'+lines[i])
            if restaurant[0] in lines[i]:
                print("in")
                lines[i] = lines[i].replace(restaurant[0],restaurant[1],1)
                break
            else:
                print("not in")
    #print(lines)
    with open(basicPath+'/'+list,'w') as f:
        f.writelines(lines)