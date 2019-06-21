/**
 * Enforces a deserialize method to ensure a model class can construct itself from a JSON string
 */
export interface Deserializable {
  deserialize(input: any): this;
}
