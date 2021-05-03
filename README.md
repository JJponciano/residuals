# residuals
Generates a transparent image in the residues are drawn from a JSON file

## Compiling 
`mvn package`

## Usage:
`[option: -d: to display the image ] <image width> <image height> <json path>`


### Example:

`java -jar target/image-residual-1.0-SNAPSHOT-spring-boot.jar -d 1000 1000 comparison_js_dummy_v3.json`

## Results
![residuals870ebfe0-42a4-4d0f-95a8-d48c92a0b261](https://user-images.githubusercontent.com/4417328/116871217-37c65400-ac14-11eb-873b-6b5da95256b6.png)

## JSON structure:
````
{
    "geometric_comparison": {
      "Member":{
        "residuals": {
          "start point": {
            "x": [250.000000,250.000000...],
            "y": [400.000000,450.000000...]
          },
          "end point": {
            "x": [232.000000,236.000000...],
            "y": [420.000000,473.000000...]
          },
      },
    },
}

