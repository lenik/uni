-- reset depth
    update user_folder set depth=0

    update user_folder set depth=1 where parent is null

    update user_folder set depth=2 where parent in
        (select id from user_folder where depth=1)
        
    update user_folder set depth=3 where parent in
        (select id from user_folder where depth=2)
        
    update user_folder set depth=4 where parent in
        (select id from user_folder where depth=3)
        
    update user_folder set depth=5 where parent in
        (select id from user_folder where depth=4)
        
    update user_folder set depth=6 where parent in
        (select id from user_folder where depth=5)

-- find possible loops    
    select * from user_folder where depth=0


