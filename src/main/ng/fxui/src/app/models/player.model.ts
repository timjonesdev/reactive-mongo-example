import {Deserializable} from "./deserializable";

export class PlayerModel implements Deserializable {

  name: string;
  score: number;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
